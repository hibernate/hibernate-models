/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

import static org.hibernate.models.spi.ClassDetails.CLASS_CLASS_DETAILS;
import static org.hibernate.models.spi.ClassDetails.OBJECT_CLASS_DETAILS;
import static org.hibernate.models.spi.ClassDetails.VOID_CLASS_DETAILS;
import static org.hibernate.models.spi.ClassDetails.VOID_OBJECT_CLASS_DETAILS;

/**
 * Base ClassDetailsRegistry implementation support
 *
 * @author Steve Ebersole
 */
public abstract class AbstractClassDetailsRegistry implements MutableClassDetailsRegistry {
	@Nonnull
	protected final ModelsContext context;
	private final boolean trackImplementors;

	@Nonnull
	protected final Map<String, ClassDetails> classDetailsMap;

	// class -> subclasses
	@Nonnull
	protected final Map<String, Set<ClassDetails>> directSubtypeMap;
	// interface -> implementations (and specializations)
	@Nonnull
	protected final Map<String, Set<ClassDetails>> directImplementorMap;

	protected AbstractClassDetailsRegistry(boolean trackImplementors, @Nonnull ModelsContext context) {
		this( trackImplementors, new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), context );
	}

	protected AbstractClassDetailsRegistry(
			boolean trackImplementors,
			@Nonnull Map<String, ClassDetails> classDetailsMap,
			@Nonnull Map<String, Set<ClassDetails>> directSubtypeMap,
			@Nonnull Map<String, Set<ClassDetails>> directImplementorMap,
			@Nonnull ModelsContext context) {
		this.trackImplementors = trackImplementors;
		this.classDetailsMap = classDetailsMap;
		this.directSubtypeMap = directSubtypeMap;
		this.directImplementorMap = directImplementorMap;
		this.context = context;

		classDetailsMap.put( CLASS_CLASS_DETAILS.getName(), CLASS_CLASS_DETAILS );
		classDetailsMap.put( OBJECT_CLASS_DETAILS.getClassName(), OBJECT_CLASS_DETAILS );
		classDetailsMap.put( VOID_CLASS_DETAILS.getClassName(), VOID_CLASS_DETAILS );
		classDetailsMap.put( VOID_OBJECT_CLASS_DETAILS.getClassName(), VOID_OBJECT_CLASS_DETAILS );
	}

	@Override
	public boolean isTrackingImplementors() {
		return trackImplementors;
	}

	@Nonnull
	@Override
	public List<ClassDetails> getDirectSubTypes(@Nonnull String typeName) {
		final Set<ClassDetails> directSubtypes = getDirectSubtypes( typeName );
		return CollectionHelper.isNotEmpty( directSubtypes )
				? new ArrayList<>( directSubtypes )
				: List.of();
	}

	@Nonnull
	@Override
	public Set<ClassDetails> getDirectSubtypes(@Nonnull String typeName) {
		final Set<ClassDetails> directSubtypes = directSubtypeMap.get( typeName );
		return directSubtypes != null ? directSubtypes : Set.of();
	}

	@Override
	public void forEachDirectSubtype(@Nonnull String typeName, @Nonnull ClassDetailsConsumer consumer) {
		final List<ClassDetails> directSubTypes = getDirectSubTypes( typeName );
		if ( directSubTypes == null ) {
			return;
		}
		for ( int i = 0; i < directSubTypes.size(); i++ ) {
			consumer.consume( directSubTypes.get( i ) );
		}
	}

	@Nonnull
	@Override
	public Set<ClassDetails> getDirectImplementors(@Nonnull String interfaceName) {
		if ( !trackImplementors ) {
			return Collections.emptySet();
		}

		final Set<ClassDetails> implementors = directImplementorMap.get( interfaceName );
		return implementors != null ? implementors : Set.of();
	}

	@Override
	public void forEachDirectImplementor(@Nonnull String interfaceName, @Nonnull ClassDetailsConsumer consumer) {
		if ( !trackImplementors ) {
			return;
		}

		final Set<ClassDetails> directImplementors = getDirectImplementors( interfaceName );
		if ( directImplementors != null ) {
			directImplementors.forEach( consumer::consume );
		}
	}

	@Override
	public void walkConcreteTypes(@Nonnull String base, boolean includeBase, @Nonnull ClassDetailsConsumer consumer) {
		walkImplementors( base, includeBase, classDetails -> {
			if ( !classDetails.isAbstract() && !classDetails.isInterface() ) {
				consumer.consume( classDetails );
			}
		});
	}

	@Nonnull
	@Override
	public Set<ClassDetails> collectImplementors(@Nonnull String base, boolean includeBase, @Nullable Predicate<ClassDetails> exclusions) {
		final Set<ClassDetails> result = new LinkedHashSet<>();
		walkImplementors( base, includeBase, classDetails -> {
			if ( exclusions == null || !exclusions.test( classDetails ) ) {
				result.add( classDetails );
			}
		} );
		return result;
	}

	@Override
	public void walkImplementors(@Nonnull String base, boolean includeBase, @Nonnull ClassDetailsConsumer consumer) {
		if ( includeBase ) {
			final ClassDetails baseDetails = resolveClassDetails( base );
			consumer.consume( baseDetails );
		}

		forEachDirectSubtype( base, (subType) -> {
			consumer.consume( subType );
			walkSubtypes( subType, consumer );
		} );

		if ( trackImplementors ) {
			forEachDirectImplementor( base, (implementor) -> {
				consumer.consume( implementor );
				walkInterfaceImplementors( implementor, consumer );
			} );
		}
	}

	private void walkSubtypes(ClassDetails base, ClassDetailsConsumer consumer) {
		forEachDirectSubtype( base.getName(), (subType) -> {
			consumer.consume( subType );
			walkSubtypes( subType, consumer );
		} );
	}

	private void walkInterfaceImplementors(ClassDetails implementor, ClassDetailsConsumer consumer) {
		if ( implementor.isInterface() ) {
			// the direct interface implementor is itself an interface...
			forEachDirectImplementor( implementor.getName(), (implementorImplementor) -> {
				consumer.consume( implementorImplementor );
				walkInterfaceImplementors( implementorImplementor, consumer );
			} );
		}
		else {
			// the direct interface implementor is itself a class...
			forEachDirectSubtype( implementor.getName(), (subtype) -> {
				consumer.consume( subtype );
				walkSubtypes( subtype, consumer );
			} );
		}
	}

	@Nullable
	@Override
	public ClassDetails findClassDetails(@Nonnull String name) {
		return classDetailsMap.get( name );
	}

	@Override
	public void forEachClassDetails(@Nonnull ClassDetailsConsumer consumer) {
		for ( Map.Entry<String, ClassDetails> entry : classDetailsMap.entrySet() ) {
			consumer.consume( entry.getValue() );
		}
	}

	@Nonnull
	@Override
	public Stream<ClassDetails> streamClassDetails() {
		return classDetailsMap.values().stream();
	}

	@Nonnull
	@Override
	public ClassDetails resolveClassDetails(@Nonnull String name) {
		if ( name == null ) {
			throw new IllegalArgumentException( "`name` cannot be null" );
		}

		final ClassDetails existing = classDetailsMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		return createClassDetails( name );
	}

	@Nonnull
	@Override
	public ClassDetails resolvePackageDetails(@Nonnull String packageName) {
		final ClassDetails existing = findPackageDetails( packageName );
		if ( existing != null ) {
			return existing;
		}

		final String packageInfoName = packageName + ".package-info";
		final ClassDetails descriptor = buildPackageDetails( packageInfoName );
		final ClassDetails details = descriptor == null
				? new MissingPackageInfoDetails( packageName, packageInfoName )
				: descriptor;
		addClassDetails( packageInfoName, details );
		return details;
	}

	/**
	 * Builds package descriptor details, returning {@code null} only when the descriptor
	 * is absent. Backends may override this to locate descriptors without reflection.
	 * Loading and construction failures must propagate rather than indicate absence.
	 */
	@Nullable
	protected ClassDetails buildPackageDetails(@Nonnull String packageInfoName) {
		final Class<?> descriptorClass = context.getClassLoading().findClassForName( packageInfoName );
		return descriptorClass == null ? null : JdkBuilders.buildClassDetailsStatic( descriptorClass, context );
	}

	@Nonnull
	@Override
	@SuppressWarnings({ "deprecation", "removal" })
	public ClassDetails resolveClassOrPackageDetails(@Nonnull String name) {
		if ( name == null ) {
			throw new IllegalArgumentException( "`name` cannot be null" );
		}

		final ClassDetails existing = classDetailsMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		if ( name.endsWith( ".package-info" ) ) {
			return createClassDetailsExact( name, name );
		}

		try {
			return createClassDetailsExact( name, name );
		}
		catch (UnknownClassException unknownClass) {
			final String packageInfoName = name + ".package-info";
			final ClassDetails existingPackage = classDetailsMap.get( packageInfoName );
			if ( existingPackage != null ) {
				return existingPackage;
			}

			try {
				return createClassDetailsExact( packageInfoName, packageInfoName );
			}
			catch (UnknownClassException unknownPackage) {
				throw unknownClass;
			}
		}
	}

	@Nonnull
	protected ClassDetails createClassDetails(@Nonnull String name) {
		return createClassDetailsExact( name, name );
	}

	@Nonnull
	protected ClassDetails createClassDetailsExact(@Nonnull String name, @Nonnull String registrationName) {
		final ClassDetails created = buildClassDetails( name );
		addClassDetails( registrationName, created );
		return created;
	}

	@Nonnull
	protected ClassDetails buildClassDetails(@Nonnull String name) {
		final ClassDetails classDetails = getClassDetailsBuilder().buildClassDetails( name, context );
		if ( classDetails == null ) {
			throw new UnknownClassException( "Unable to resolve ClassDetails for `" + name + "`" );
		}
		return classDetails;
	}

	@Override
	public void addClassDetails(@Nonnull ClassDetails classDetails) {
		addClassDetails( classDetails.getClassName(), classDetails );
	}

	@Override
	public void addClassDetails(@Nonnull String name, @Nonnull ClassDetails classDetails) {
		classDetailsMap.put( name, classDetails );

		if ( classDetails.getSuperClass() != null ) {
			Set<ClassDetails> subTypes = directSubtypeMap.get( classDetails.getSuperClass().getName() );
			//noinspection Java8MapApi
			if ( subTypes == null ) {
				subTypes = new LinkedHashSet<>();
				directSubtypeMap.put( classDetails.getSuperClass().getName(), subTypes );
			}
			subTypes.add( classDetails );
		}

		if ( trackImplementors ) {
			final List<TypeDetails> implementedInterfaces = classDetails.getImplementedInterfaces();
			if ( implementedInterfaces != null ) {
				implementedInterfaces.forEach( (implementedInterface) -> {
					final Set<ClassDetails> directImplementors = directImplementorMap.computeIfAbsent(
							implementedInterface.getName(),
							(interfaceName) -> new LinkedHashSet<>()
					);
					directImplementors.add( classDetails );
				} );
			}
		}
	}

	@Nullable
	@Override
	public ClassDetails resolveClassDetails(@Nonnull String name, @Nonnull ClassDetailsCreator creator) {
		if ( name == null ) {
			throw new IllegalArgumentException( "`name` cannot be null" );
		}

		if ( "void".equals( name ) ) {
			return null;
		}

		final ClassDetails existing = classDetailsMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		return createClassDetails( name, creator );
	}

	@Nonnull
	protected ClassDetails createClassDetails(@Nonnull String name, @Nonnull ClassDetailsCreator creator) {
		final ClassDetails created = creator.createClassDetails( name );
		addClassDetails( name, created );
		return created;
	}

	@Nonnull
	public Map<String, ClassDetails> classDetailsMap() {
		return classDetailsMap;
	}

	@Nonnull
	public Map<String, ClassDetails> getClassDetailsMap() {
		return Collections.unmodifiableMap( classDetailsMap );
	}

	@Nonnull
	public Map<String, Set<ClassDetails>> getDirectSubTypeMap() {
		return Collections.unmodifiableMap( directSubtypeMap );
	}

	@Nonnull
	public Map<String, Set<ClassDetails>> getDirectImplementorMap() {
		return Collections.unmodifiableMap( directImplementorMap );
	}
}
