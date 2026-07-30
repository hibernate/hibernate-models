/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.spi;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Resolves a {@link Lookup} with full privilege access for a target class, even when
 * the target class is in a different {@link ClassLoader} or {@link Module} than the caller.
 * <p>
 * <b>Why this is needed:</b>
 * <p>
 * On Java 9+, {@link MethodHandles#privateLookupIn(Class, Lookup)} drops the
 * {@code MODULE} access bit from the returned lookup whenever the lookup crosses a
 * module boundary — whether between two unnamed modules (different classloaders) or
 * between named JPMS modules (same classloader). See
 * <a href="https://bugs.openjdk.org/browse/JDK-8228624">JDK-8228624</a>:
 * <em>"Teleporting across modules will always record the original lookup class as
 * the previous lookup class and drops MODULE access."</em>
 * <p>
 * {@link Lookup#defineHiddenClass(byte[], boolean, Lookup.ClassOption...)} requires
 * "full privilege access" (both {@code PRIVATE} and {@code MODULE} bits set), so it
 * fails with {@code IllegalAccessException} when given a lookup that crossed a module
 * boundary.
 * <p>
 * This is not a JDK bug; it is by design. The cross-module lookup retains
 * {@code PRIVATE + PACKAGE} access (sufficient for {@code unreflect*} and
 * {@link Lookup#defineClass(byte[])}), but not {@code MODULE} (required for
 * {@code defineHiddenClass}).
 * <p>
 * <b>How the bridge works:</b>
 * <ol>
 *   <li>Detect whether the target class is in the same classloader <em>and</em> the
 *       same module as the caller. If so, use {@code privateLookupIn} directly
 *       (zero overhead).</li>
 *   <li>Otherwise, obtain a cross-boundary lookup via {@code privateLookupIn}
 *       (retains {@code PACKAGE} access).</li>
 *   <li>Use {@link Lookup#defineClass(byte[])} (which only requires {@code PACKAGE}
 *       access) to inject a small bridge class into the target module.</li>
 *   <li>The bridge class has a static method that calls
 *       {@link MethodHandles#lookup()} — a caller-sensitive method that returns a
 *       lookup scoped to the bridge class's own module. Since the bridge lives in
 *       the target module, the returned lookup has full privilege access
 *       ({@code PRIVATE + MODULE}) for that module.</li>
 *   <li>Use the bridge lookup with {@code privateLookupIn} for the actual target
 *       class — since both are now in the same module, the {@code MODULE}
 *       bit is retained.</li>
 * </ol>
 * <p>
 * The bridge is cached per {@code (ClassLoader, Module)} pair: one bridge class per
 * foreign module, reused for all target classes in that module. The outer cache uses
 * weak keys on {@code ClassLoader} so that foreign classloaders can be
 * garbage-collected when they are no longer in use (e.g. on application redeployment).
 * <p>
 * This affects:
 * <ul>
 *   <li>App servers (WildFly, WebLogic, Tomcat), OSGi containers, and bytecode
 *       enhancement classloaders — any scenario where entity classes are loaded by a
 *       different classloader than Hibernate.</li>
 *   <li>JPMS modular applications — where entity classes live in a different named
 *       module than Hibernate (e.g. {@code my.entities} vs {@code org.hibernate.orm}).</li>
 * </ul>
 *
 * @see <a href="https://bugs.openjdk.org/browse/JDK-8228624">JDK-8228624</a>
 * @see <a href="https://bugs.openjdk.org/browse/JDK-8233726">JDK-8233726</a>
 */
public final class CrossClassLoaderLookupBridge {

	/**
	 * Simple name of the bridge class injected into foreign classloaders.
	 */
	public static final String BRIDGE_CLASS_SIMPLE_NAME = "$$HibernateAccessorBridge";

	/**
	 * Name of the public static method on the bridge class that returns a
	 * {@link Lookup} scoped to the bridge class's module.
	 */
	public static final String BRIDGE_METHOD_NAME = "$$lookupBridge";

	private final Map<ClassLoader, ConcurrentHashMap<Module, Lookup>> bridgeCache =
			Collections.synchronizedMap( new WeakHashMap<>() );
	private final Lookup callerLookup;
	private final Function<String, byte[]> bridgeBytecodeGenerator;

	/**
	 * @param callerLookup the lookup from the accessor factory's own context
	 * @param bridgeBytecodeGenerator generates bridge class bytecode for a given fully-qualified
	 *        class name. The generated class must be {@code public}, extend {@code Object},
	 *        and have a {@code public static} method named {@value #BRIDGE_METHOD_NAME}
	 *        that returns {@link MethodHandles#lookup()}.
	 */
	public CrossClassLoaderLookupBridge(Lookup callerLookup, Function<String, byte[]> bridgeBytecodeGenerator) {
		this.callerLookup = callerLookup;
		this.bridgeBytecodeGenerator = bridgeBytecodeGenerator;
	}

	/**
	 * Returns a {@link Lookup} with full privilege access for the given target class,
	 * suitable for {@link Lookup#defineHiddenClass(byte[], boolean, Lookup.ClassOption...)}.
	 * <p>
	 * When the target class is in the same classloader and module as the caller, this is
	 * equivalent to {@code MethodHandles.privateLookupIn(targetClass, callerLookup)}.
	 * When the classloader or module differs, a bridge is used to obtain a lookup with the
	 * {@code MODULE} access bit intact.
	 */
	public Lookup resolve(Class<?> targetClass) throws IllegalAccessException {
		if ( targetClass.getClassLoader() == callerLookup.lookupClass().getClassLoader()
				&& targetClass.getModule() == callerLookup.lookupClass().getModule() ) {
			return MethodHandles.privateLookupIn( targetClass, callerLookup );
		}
		final ConcurrentHashMap<Module, Lookup> moduleLookups = bridgeCache.computeIfAbsent(
				targetClass.getClassLoader(), k -> new ConcurrentHashMap<>()
		);
		final Lookup bridgeLookup = moduleLookups.computeIfAbsent(
				targetClass.getModule(), m -> createBridgeLookup( targetClass )
		);
		return MethodHandles.privateLookupIn( targetClass, bridgeLookup );
	}

	private Lookup createBridgeLookup(Class<?> targetClass) {
		try {
			Lookup crossClLookup = MethodHandles.privateLookupIn( targetClass, callerLookup );
			String pkg = targetClass.getPackageName();
			String bridgeClassName = pkg.isEmpty() ? BRIDGE_CLASS_SIMPLE_NAME : pkg + "." + BRIDGE_CLASS_SIMPLE_NAME;
			Class<?> bridgeClass;
			try {
				byte[] bridgeBytecode = bridgeBytecodeGenerator.apply( bridgeClassName );
				bridgeClass = crossClLookup.defineClass( bridgeBytecode );
			}
			catch (LinkageError e) {
				// Another factory instance already defined the bridge in this classloader —
				// reuse the existing class (defineClass creates non-hidden named classes,
				// so a second defineClass for the same name throws LinkageError)
				bridgeClass = targetClass.getClassLoader().loadClass( bridgeClassName );
			}
			Method lookupMethod = bridgeClass.getMethod( BRIDGE_METHOD_NAME );
			return (Lookup) lookupMethod.invoke( null );
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(
					"Cannot create lookup bridge for classloader of " + targetClass.getName()
							+ ": privateLookupIn failed across module boundary", e );
		}
		catch (Exception e) {
			throw new RuntimeException(
					"Failed to create lookup bridge for classloader of " + targetClass.getName(), e );
		}
	}
}
