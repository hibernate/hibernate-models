/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.tck.tests.crossclassloader;

import org.hibernate.models.accessor.tck.tests.beans.visibility.PropertyVisibilityBean;
import org.hibernate.models.accessor.tck.util.IsolatingClassLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Diagnostic test that directly exercises JDK MethodHandles.Lookup APIs
 * across a classloader boundary, WITHOUT using any accessor factory.
 * <p>
 * The goal is to document precisely which JDK operations succeed and which
 * fail when a Lookup obtained via {@code MethodHandles.privateLookupIn()}
 * targets a class loaded by a different classloader (and therefore residing
 * in a different unnamed module on Java 9+).
 * <p>
 * Specifically tested:
 * <ul>
 *   <li>Access modes of cross-CL vs same-CL lookups</li>
 *   <li>{@code defineHiddenClass} with NESTMATE (expected to fail cross-CL)</li>
 *   <li>{@code defineHiddenClass} without NESTMATE</li>
 *   <li>{@code Lookup.defineClass()} (non-hidden, needs PACKAGE access)</li>
 *   <li>{@code unreflectGetter}/{@code unreflectSetter} on private fields</li>
 * </ul>
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Cross-classloader lookup diagnostic (no accessor factory)")
public class CrossClassLoaderLookupDiagnosticTest {

	private static final String BEAN_CLASS_NAME = PropertyVisibilityBean.class.getName();

	/**
	 * Access mode bit constants from {@link MethodHandles.Lookup}.
	 */
	private static final int PUBLIC    = MethodHandles.Lookup.PUBLIC;
	private static final int PRIVATE   = MethodHandles.Lookup.PRIVATE;
	private static final int PROTECTED = MethodHandles.Lookup.PROTECTED;
	private static final int PACKAGE   = MethodHandles.Lookup.PACKAGE;
	private static final int MODULE    = MethodHandles.Lookup.MODULE;
	private static final int UNCONDITIONAL = MethodHandles.Lookup.UNCONDITIONAL;

	private Class<?> isolatedClass;
	private Object isolatedInstance;
	private MethodHandles.Lookup callerLookup;
	private MethodHandles.Lookup crossClLookup;
	private MethodHandles.Lookup sameClLookup;

	@BeforeAll
	void setup() throws Exception {
		// 1. Load PropertyVisibilityBean in an isolating classloader
		IsolatingClassLoader isolatingLoader = new IsolatingClassLoader(
				Set.of( BEAN_CLASS_NAME ),
				getClass().getClassLoader()
		);
		isolatedClass = isolatingLoader.loadClass( BEAN_CLASS_NAME );

		assertNotSame( PropertyVisibilityBean.class, isolatedClass,
				"Isolated class must be a distinct Class object" );
		assertNotSame( PropertyVisibilityBean.class.getModule(), isolatedClass.getModule(),
				"Isolated class must be in a different unnamed module" );

		var ctor = isolatedClass.getDeclaredConstructor();
		ctor.setAccessible( true );
		isolatedInstance = ctor.newInstance();

		// 2. Obtain a caller lookup from this test class's context
		callerLookup = MethodHandles.lookup();

		// 3. Obtain a cross-classloader privateLookupIn
		crossClLookup = MethodHandles.privateLookupIn( isolatedClass, callerLookup );

		// 4. Obtain a same-classloader privateLookupIn for comparison
		sameClLookup = MethodHandles.privateLookupIn( PropertyVisibilityBean.class, callerLookup );
	}

	@Test
	@DisplayName("1. Compare access modes: cross-CL vs same-CL lookup")
	void testAccessModes() {
		int crossModes = crossClLookup.lookupModes();
		int sameModes = sameClLookup.lookupModes();

		System.out.println( "=== Access Mode Comparison ===" );
		System.out.println( "Caller lookup class:    " + callerLookup.lookupClass().getName() );
		System.out.println( "Caller lookup modes:    " + describeModes( callerLookup.lookupModes() ) );
		System.out.println();
		System.out.println( "Cross-CL lookup class:  " + crossClLookup.lookupClass().getName() );
		System.out.println( "Cross-CL lookup modes:  " + describeModes( crossModes ) );
		System.out.println( "Cross-CL classloader:   " + isolatedClass.getClassLoader().getClass().getName() );
		System.out.println( "Cross-CL module:        " + isolatedClass.getModule() );
		System.out.println();
		System.out.println( "Same-CL lookup class:   " + sameClLookup.lookupClass().getName() );
		System.out.println( "Same-CL lookup modes:   " + describeModes( sameModes ) );
		System.out.println( "Same-CL classloader:    " + PropertyVisibilityBean.class.getClassLoader().getClass().getName() );
		System.out.println( "Same-CL module:         " + PropertyVisibilityBean.class.getModule() );
		System.out.println();

		// Both should have PRIVATE access
		assertTrue( (crossModes & PRIVATE) != 0,
				"Cross-CL lookup should have PRIVATE access, modes=" + describeModes( crossModes ) );
		assertTrue( (sameModes & PRIVATE) != 0,
				"Same-CL lookup should have PRIVATE access, modes=" + describeModes( sameModes ) );

		// Check MODULE bit — cross-CL typically loses MODULE since it crosses module boundaries
		boolean crossHasModule = (crossModes & MODULE) != 0;
		boolean sameHasModule = (sameModes & MODULE) != 0;
		System.out.println( "Cross-CL has MODULE bit: " + crossHasModule );
		System.out.println( "Same-CL has MODULE bit:  " + sameHasModule );
		System.out.println();

		// Document the difference
		int diff = sameModes & ~crossModes;
		if ( diff != 0 ) {
			System.out.println( "Modes present in same-CL but MISSING in cross-CL: " + describeModes( diff ) );
		}
		else {
			System.out.println( "Both lookups have identical access modes." );
		}
	}

	@Test
	@DisplayName("2. defineHiddenClass WITH NESTMATE on cross-CL lookup")
	void testDefineHiddenClassWithNestmateCrossCL() {
		byte[] classBytes = generateMinimalClass(
				isolatedClass.getPackageName() + ".DiagHiddenNestmate"
		);

		System.out.println( "=== defineHiddenClass WITH NESTMATE (cross-CL) ===" );
		try {
			MethodHandles.Lookup result = crossClLookup.defineHiddenClass(
					classBytes, true,
					MethodHandles.Lookup.ClassOption.NESTMATE
			);
			System.out.println( "SUCCESS: hidden class defined as nestmate: " + result.lookupClass().getName() );
			System.out.println( "  Hidden class loader:  " + result.lookupClass().getClassLoader() );
			System.out.println( "  Hidden class module:  " + result.lookupClass().getModule() );
			System.out.println( "  Hidden class nest host: " + result.lookupClass().getNestHost().getName() );
			// If this succeeds, we note it — it may or may not depending on JDK version
		}
		catch ( Exception e ) {
			System.out.println( "FAILED with: " + e.getClass().getName() );
			System.out.println( "  Message: " + e.getMessage() );
			// This is the expected outcome for cross-CL: defineHiddenClass with NESTMATE
			// requires MODULE access, which is lost when crossing module boundaries
			assertTrue( e instanceof IllegalAccessException,
					"Expected IllegalAccessException for missing full privilege access" );
		}
	}

	@Test
	@DisplayName("3. defineHiddenClass WITHOUT NESTMATE on cross-CL lookup")
	void testDefineHiddenClassWithoutNestmateCrossCL() {
		byte[] classBytes = generateMinimalClass(
				isolatedClass.getPackageName() + ".DiagHiddenNoNestmate"
		);

		System.out.println( "=== defineHiddenClass WITHOUT NESTMATE (cross-CL) ===" );
		try {
			MethodHandles.Lookup result = crossClLookup.defineHiddenClass(
					classBytes, true
					// no ClassOption — no NESTMATE
			);
			System.out.println( "SUCCESS: hidden class defined (no nestmate): " + result.lookupClass().getName() );
			System.out.println( "  Hidden class loader:  " + result.lookupClass().getClassLoader() );
			System.out.println( "  Hidden class module:  " + result.lookupClass().getModule() );
		}
		catch ( Exception e ) {
			// KEY FINDING: defineHiddenClass requires "full privilege access" which
			// means the MODULE bit (0x10) must be present. Cross-CL lookups lose
			// the MODULE bit, so ALL defineHiddenClass calls fail — not just NESTMATE ones.
			System.out.println( "FAILED with: " + e.getClass().getName() );
			System.out.println( "  Message: " + e.getMessage() );
			System.out.println( "  NOTE: defineHiddenClass requires FULL PRIVILEGE ACCESS (MODULE bit)." );
			System.out.println( "        Cross-CL lookups lose MODULE, so ALL defineHiddenClass calls fail." );
			assertTrue( e instanceof IllegalAccessException,
					"Expected IllegalAccessException for missing full privilege access" );
		}
	}

	@Test
	@DisplayName("4. Lookup.defineClass (non-hidden) on cross-CL lookup")
	void testDefineClassCrossCL() {
		// defineClass requires PACKAGE access — let's see if the cross-CL lookup has it
		String className = isolatedClass.getPackageName() + ".DiagDefinedClass";
		byte[] classBytes = generateMinimalClass( className );

		System.out.println( "=== Lookup.defineClass (non-hidden, cross-CL) ===" );
		System.out.println( "Cross-CL lookup has PACKAGE: " + ((crossClLookup.lookupModes() & PACKAGE) != 0) );
		try {
			Class<?> defined = crossClLookup.defineClass( classBytes );
			System.out.println( "SUCCESS: class defined: " + defined.getName() );
			System.out.println( "  Defined class loader: " + defined.getClassLoader() );
			System.out.println( "  Defined class module: " + defined.getModule() );
			assertEquals( isolatedClass.getClassLoader(), defined.getClassLoader(),
					"defineClass should use the lookup class's classloader" );
		}
		catch ( Exception e ) {
			System.out.println( "FAILED with: " + e.getClass().getName() );
			System.out.println( "  Message: " + e.getMessage() );
			fail( "Cross-CL defineClass should succeed (only needs PACKAGE access), but got: " + e );
		}
	}

	@Test
	@DisplayName("5. defineHiddenClass WITH NESTMATE on same-CL lookup (baseline)")
	void testDefineHiddenClassWithNestmateSameCL() {
		byte[] classBytes = generateMinimalClass(
				PropertyVisibilityBean.class.getPackageName() + ".DiagHiddenSameCL"
		);

		System.out.println( "=== defineHiddenClass WITH NESTMATE (same-CL, baseline) ===" );
		try {
			MethodHandles.Lookup result = sameClLookup.defineHiddenClass(
					classBytes, true,
					MethodHandles.Lookup.ClassOption.NESTMATE
			);
			System.out.println( "SUCCESS: hidden class defined as nestmate: " + result.lookupClass().getName() );
			System.out.println( "  Hidden class loader:  " + result.lookupClass().getClassLoader() );
			System.out.println( "  Hidden class module:  " + result.lookupClass().getModule() );
			System.out.println( "  Hidden class nest host: " + result.lookupClass().getNestHost().getName() );
		}
		catch ( Exception e ) {
			System.out.println( "FAILED with: " + e.getClass().getName() );
			System.out.println( "  Message: " + e.getMessage() );
			fail( "Same-CL defineHiddenClass with NESTMATE should succeed, but got: " + e );
		}
	}

	@Test
	@DisplayName("6. unreflectGetter/unreflectSetter on private fields (cross-CL)")
	void testUnreflectPrivateFieldsCrossCL() throws Throwable {
		Field privateField = isolatedClass.getDeclaredField( "privateField" );
		privateField.setAccessible( true );

		System.out.println( "=== unreflectGetter/unreflectSetter on private field (cross-CL) ===" );
		System.out.println( "Field: " + privateField );
		System.out.println( "Field declaring class loader: " + privateField.getDeclaringClass().getClassLoader() );
		System.out.println( "Cross-CL lookup modes: " + describeModes( crossClLookup.lookupModes() ) );

		// Try unreflectGetter
		try {
			MethodHandle getter = crossClLookup.unreflectGetter( privateField );
			System.out.println( "unreflectGetter SUCCESS: " + getter );

			// Actually invoke it
			Object value = getter.invoke( isolatedInstance );
			System.out.println( "  getter.invoke() returned: " + value + " (expected null for uninitialized String)" );
		}
		catch ( Exception e ) {
			System.out.println( "unreflectGetter FAILED: " + e.getClass().getName() + ": " + e.getMessage() );
		}

		// Try unreflectSetter
		try {
			MethodHandle setter = crossClLookup.unreflectSetter( privateField );
			System.out.println( "unreflectSetter SUCCESS: " + setter );

			// Actually invoke it
			setter.invoke( isolatedInstance, "cross-cl-value" );
			Object readBack = privateField.get( isolatedInstance );
			System.out.println( "  setter.invoke() wrote 'cross-cl-value', read back: " + readBack );
			assertEquals( "cross-cl-value", readBack,
					"Should be able to write and read back via unreflected setter" );
		}
		catch ( Throwable e ) {
			System.out.println( "unreflectSetter FAILED: " + e.getClass().getName() + ": " + e.getMessage() );
		}
	}

	@Test
	@DisplayName("7. unreflectGetter/unreflectSetter on private fields (same-CL baseline)")
	void testUnreflectPrivateFieldsSameCL() throws Exception {
		PropertyVisibilityBean sameClInstance = new PropertyVisibilityBean();
		Field privateField = PropertyVisibilityBean.class.getDeclaredField( "privateField" );
		privateField.setAccessible( true );

		System.out.println( "=== unreflectGetter/unreflectSetter on private field (same-CL baseline) ===" );
		System.out.println( "Same-CL lookup modes: " + describeModes( sameClLookup.lookupModes() ) );

		try {
			MethodHandle getter = sameClLookup.unreflectGetter( privateField );
			System.out.println( "unreflectGetter SUCCESS: " + getter );

			MethodHandle setter = sameClLookup.unreflectSetter( privateField );
			System.out.println( "unreflectSetter SUCCESS: " + setter );

			setter.invoke( sameClInstance, "same-cl-value" );
			Object value = getter.invoke( sameClInstance );
			System.out.println( "  Round-trip: wrote 'same-cl-value', read back: " + value );
			assertEquals( "same-cl-value", value );
		}
		catch ( Throwable e ) {
			System.out.println( "FAILED: " + e.getClass().getName() + ": " + e.getMessage() );
			fail( "Same-CL unreflect should always work, but got: " + e );
		}
	}

	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Helpers
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Generates minimal valid bytecode for a class with the given fully-qualified name.
	 * The class extends Object and has only a default constructor.
	 * <p>
	 * This is hand-crafted bytecode (Java class file format) to avoid requiring
	 * ASM or any other bytecode library as a dependency.
	 */
	private static byte[] generateMinimalClass(String className) {
		// We construct a minimal class file by hand.
		// Class file format reference: https://docs.oracle.com/javase/specs/jvms/se21/html/jvms-4.html
		//
		// Constant pool entries:
		//   #1 = Methodref       #2.#3    (java/lang/Object.<init>:()V)
		//   #2 = Class           #4       (java/lang/Object)
		//   #3 = NameAndType     #5:#6    (<init>:()V)
		//   #4 = Utf8            java/lang/Object
		//   #5 = Utf8            <init>
		//   #6 = Utf8            ()V
		//   #7 = Utf8            Code
		//   #8 = Class           #9       (our class)
		//   #9 = Utf8            our/class/Name

		String internalName = className.replace( '.', '/' );

		var buf = new java.io.ByteArrayOutputStream( 200 );

		// Magic number
		writeU4( buf, 0xCAFEBABE );
		// Version: Java 17 (61.0)
		writeU2( buf, 0 );  // minor
		writeU2( buf, 61 ); // major (Java 17)

		// Constant pool count = 10 (entries 1..9)
		writeU2( buf, 10 );

		// #1: Methodref -> class=#2, nameAndType=#3
		buf.write( 10 ); // CONSTANT_Methodref
		writeU2( buf, 2 );
		writeU2( buf, 3 );

		// #2: Class -> name=#4
		buf.write( 7 ); // CONSTANT_Class
		writeU2( buf, 4 );

		// #3: NameAndType -> name=#5, descriptor=#6
		buf.write( 12 ); // CONSTANT_NameAndType
		writeU2( buf, 5 );
		writeU2( buf, 6 );

		// #4: Utf8 "java/lang/Object"
		writeUtf8( buf, "java/lang/Object" );

		// #5: Utf8 "<init>"
		writeUtf8( buf, "<init>" );

		// #6: Utf8 "()V"
		writeUtf8( buf, "()V" );

		// #7: Utf8 "Code"
		writeUtf8( buf, "Code" );

		// #8: Class -> name=#9
		buf.write( 7 ); // CONSTANT_Class
		writeU2( buf, 9 );

		// #9: Utf8 (our class internal name)
		writeUtf8( buf, internalName );

		// Access flags: ACC_PUBLIC | ACC_SUPER = 0x0021
		writeU2( buf, 0x0021 );

		// This class: #8
		writeU2( buf, 8 );

		// Super class: #2 (java/lang/Object)
		writeU2( buf, 2 );

		// Interfaces count: 0
		writeU2( buf, 0 );

		// Fields count: 0
		writeU2( buf, 0 );

		// Methods count: 1 (default constructor)
		writeU2( buf, 1 );

		// Method: <init>()V
		writeU2( buf, 0x0001 ); // ACC_PUBLIC
		writeU2( buf, 5 );     // name: #5 "<init>"
		writeU2( buf, 6 );     // descriptor: #6 "()V"
		writeU2( buf, 1 );     // attributes_count: 1 (Code)

		// Code attribute
		writeU2( buf, 7 );     // attribute_name_index: #7 "Code"
		// Code attribute length: 17 bytes
		//   max_stack(2) + max_locals(2) + code_length(4) + code(5) + exception_table_length(2) + attributes_count(2) = 17
		writeU4( buf, 17 );
		writeU2( buf, 1 );     // max_stack
		writeU2( buf, 1 );     // max_locals
		writeU4( buf, 5 );     // code_length
		// Bytecode: aload_0, invokespecial #1, return
		buf.write( 0x2A );    // aload_0
		buf.write( 0xB7 );    // invokespecial
		writeU2( buf, 1 );    // -> #1 (Object.<init>)
		buf.write( 0xB1 );    // return
		writeU2( buf, 0 );     // exception_table_length
		writeU2( buf, 0 );     // attributes_count (of Code)

		// Class attributes count: 0
		writeU2( buf, 0 );

		return buf.toByteArray();
	}

	private static void writeU2(java.io.ByteArrayOutputStream buf, int value) {
		buf.write( (value >> 8) & 0xFF );
		buf.write( value & 0xFF );
	}

	private static void writeU4(java.io.ByteArrayOutputStream buf, int value) {
		buf.write( (value >> 24) & 0xFF );
		buf.write( (value >> 16) & 0xFF );
		buf.write( (value >> 8) & 0xFF );
		buf.write( value & 0xFF );
	}

	private static void writeUtf8(java.io.ByteArrayOutputStream buf, String s) {
		byte[] bytes = s.getBytes( java.nio.charset.StandardCharsets.UTF_8 );
		buf.write( 1 ); // CONSTANT_Utf8
		writeU2( buf, bytes.length );
		buf.write( bytes, 0, bytes.length );
	}

	/**
	 * Produces a human-readable description of lookup access mode bits.
	 */
	private static String describeModes(int modes) {
		var sb = new StringBuilder();
		sb.append( "0x" ).append( Integer.toHexString( modes ) ).append( " [" );
		boolean first = true;
		if ( (modes & PUBLIC) != 0 ) {
			sb.append( "PUBLIC" );
			first = false;
		}
		if ( (modes & PRIVATE) != 0 ) {
			if ( !first ) {
				sb.append( ", " );
			}
			sb.append( "PRIVATE" );
			first = false;
		}
		if ( (modes & PROTECTED) != 0 ) {
			if ( !first ) {
				sb.append( ", " );
			}
			sb.append( "PROTECTED" );
			first = false;
		}
		if ( (modes & PACKAGE) != 0 ) {
			if ( !first ) {
				sb.append( ", " );
			}
			sb.append( "PACKAGE" );
			first = false;
		}
		if ( (modes & MODULE) != 0 ) {
			if ( !first ) {
				sb.append( ", " );
			}
			sb.append( "MODULE" );
			first = false;
		}
		if ( (modes & UNCONDITIONAL) != 0 ) {
			if ( !first ) {
				sb.append( ", " );
			}
			sb.append( "UNCONDITIONAL" );
		}
		sb.append( "]" );
		return sb.toString();
	}
}
