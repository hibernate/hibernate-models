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

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.util.CollectionHelper;
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
	protected final ModelsContext context;
	private final boolean trackImplementors;

	protected final Map<String, ClassDetails> classDetailsMap;

	// class -> subclasses
	protected final Map<String, Set<ClassDetails>> directSubtypeMap;
	// interface -> implementations (and specializations)
	protected final Map<String, Set<ClassDetails>> directImplementorMap;

	protected AbstractClassDetailsRegistry(boolean trackImplementors, ModelsContext context) {
		this( trackImplementors, new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), context );
	}

	protected AbstractClassDetailsRegistry(
			boolean trackImplementors,
			Map<String, ClassDetails> classDetailsMap,
			Map<String, Set<ClassDetails>> directSubtypeMap,
			Map<String, Set<ClassDetails>> directImplementorMap,
			ModelsContext context) {
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

	@Override
	public List<ClassDetails> getDirectSubTypes(String typeName) {
		final Set<ClassDetails> directSubtypes = getDirectSubtypes( typeName );
		return CollectionHelper.isNotEmpty( directSubtypes )
				? new ArrayList<>( directSubtypes )
				: List.of();
	}

	@Override
	public Set<ClassDetails> getDirectSubtypes(String typeName) {
		final Set<ClassDetails> directSubtypes = directSubtypeMap.get( typeName );
		return directSubtypes != null ? directSubtypes : Set.of();
	}

	@Override
	public void forEachDirectSubtype(String typeName, ClassDetailsConsumer consumer) {
		final List<ClassDetails> directSubTypes = getDirectSubTypes( typeName );
		if ( directSubTypes == null ) {
			return;
		}
		for ( int i = 0; i < directSubTypes.size(); i++ ) {
			consumer.consume( directSubTypes.get( i ) );
		}
	}

	@Override
	public Set<ClassDetails> getDirectImplementors(String interfaceName) {
		if ( !trackImplementors ) {
			return Collections.emptySet();
		}

		final Set<ClassDetails> implementors = directImplementorMap.get( interfaceName );
		return implementors != null ? implementors : Set.of();
	}

	@Override
	public void forEachDirectImplementor(String interfaceName, ClassDetailsConsumer consumer) {
		if ( !trackImplementors ) {
			return;
		}

		final Set<ClassDetails> directImplementors = getDirectImplementors( interfaceName );
		if ( directImplementors != null ) {
			directImplementors.forEach( consumer::consume );
		}
	}

	@Override
	public void walkConcreteTypes(String base, boolean includeBase, ClassDetailsConsumer consumer) {
		walkImplementors( base, includeBase, classDetails -> {
			if ( !classDetails.isAbstract() && !classDetails.isInterface() ) {
				consumer.consume( classDetails );
			}
		});
	}

	@Override
	public Set<ClassDetails> collectImplementors(String base, boolean includeBase, Predicate<ClassDetails> exclusions) {
		final Set<ClassDetails> result = new LinkedHashSet<>();
		walkImplementors( base, includeBase, classDetails -> {
			if ( exclusions == null || !exclusions.test( classDetails ) ) {
				result.add( classDetails );
			}
		} );
		return result;
	}

	@Override
	public void walkImplementors(String base, boolean includeBase, ClassDetailsConsumer consumer) {
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

	@Override
	public ClassDetails findClassDetails(String name) {
		return classDetailsMap.get( name );
	}

	@Override
	public void forEachClassDetails(ClassDetailsConsumer consumer) {
		for ( Map.Entry<String, ClassDetails> entry : classDetailsMap.entrySet() ) {
			consumer.consume( entry.getValue() );
		}
	}

	@Override
	public ClassDetails resolveClassDetails(String name) {
		if ( name == null ) {
			throw new IllegalArgumentException( "`name` cannot be null" );
		}

		final ClassDetails existing = classDetailsMap.get( name );
		if ( existing != null ) {
			return existing;
		}

		return createClassDetails( name );
	}

	protected ClassDetails createClassDetails(String name) {
		try {
			final ClassDetails created = getClassDetailsBuilder().buildClassDetails( name, context );
			addClassDetails( name, created );
			return created;
		}
		catch (UnknownClassException e) {
			// see if it might be a package name...
			try {
				return getClassDetailsBuilder().buildClassDetails( name + ".package-info", context );
			}
			catch (UnknownClassException noPackage) {
				throw e;
			}
		}
	}

	@Override
	public void addClassDetails(ClassDetails classDetails) {
		addClassDetails( classDetails.getClassName(), classDetails );
	}

	@Override
	public void addClassDetails(String name, ClassDetails classDetails) {
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

	@Override
	public ClassDetails resolveClassDetails(String name, ClassDetailsCreator creator) {
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

	protected ClassDetails createClassDetails(String name, ClassDetailsCreator creator) {
		try {
			final ClassDetails created = creator.createClassDetails( name );
			addClassDetails( name, created );
			return created;
		}
		catch (UnknownClassException e) {
			// see if it might be a package name...
			try {
				return creator.createClassDetails( name + ".package-info" );
			}
			catch (UnknownClassException noPackage) {
				throw e;
			}
		}
	}

	public Map<String, ClassDetails> classDetailsMap() {
		return classDetailsMap;
	}

	public Map<String, ClassDetails> getClassDetailsMap() {
		return Collections.unmodifiableMap( classDetailsMap );
	}

	public Map<String, Set<ClassDetails>> getDirectSubTypeMap() {
		return Collections.unmodifiableMap( directSubtypeMap );
	}

	public Map<String, Set<ClassDetails>> getDirectImplementorMap() {
		return Collections.unmodifiableMap( directImplementorMap );
	}
}
