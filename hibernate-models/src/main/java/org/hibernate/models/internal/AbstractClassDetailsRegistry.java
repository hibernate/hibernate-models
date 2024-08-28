/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractClassDetailsRegistry implements MutableClassDetailsRegistry {
	protected final SourceModelBuildingContext context;

	protected final Map<String, ClassDetails> classDetailsMap;

	// subtype per type
	protected final Map<String, List<ClassDetails>> subTypeClassDetailsMap;

	protected AbstractClassDetailsRegistry(SourceModelBuildingContext context) {
		this( new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), context );
	}

	protected AbstractClassDetailsRegistry(
			Map<String, ClassDetails> classDetailsMap,
			Map<String, List<ClassDetails>> subTypeClassDetailsMap,
			SourceModelBuildingContext context) {
		this.classDetailsMap = classDetailsMap;
		this.subTypeClassDetailsMap = subTypeClassDetailsMap;
		this.context = context;

		classDetailsMap.put( ClassDetails.OBJECT_CLASS_DETAILS.getName(), ClassDetails.OBJECT_CLASS_DETAILS );
		classDetailsMap.put( ClassDetails.CLASS_CLASS_DETAILS.getName(), ClassDetails.CLASS_CLASS_DETAILS );
		classDetailsMap.put( ClassDetails.VOID_CLASS_DETAILS.getName(), ClassDetails.VOID_CLASS_DETAILS );
		classDetailsMap.put( ClassDetails.VOID_OBJECT_CLASS_DETAILS.getName(), ClassDetails.VOID_OBJECT_CLASS_DETAILS );
	}

	protected abstract ClassDetailsBuilder getClassDetailsBuilder();

	@Override
	public List<ClassDetails> getDirectSubTypes(String superTypeName) {
		return subTypeClassDetailsMap.get( superTypeName );
	}

	@Override
	public void forEachDirectSubType(String superTypeName, ClassDetailsConsumer consumer) {
		final List<ClassDetails> directSubTypes = getDirectSubTypes( superTypeName );
		if ( directSubTypes == null ) {
			return;
		}
		for ( int i = 0; i < directSubTypes.size(); i++ ) {
			consumer.consume( directSubTypes.get( i ) );
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
			List<ClassDetails> subTypes = subTypeClassDetailsMap.get( classDetails.getSuperClass().getName() );
			//noinspection Java8MapApi
			if ( subTypes == null ) {
				subTypes = new ArrayList<>();
				subTypeClassDetailsMap.put( classDetails.getSuperClass().getName(), subTypes );
			}
			subTypes.add( classDetails );
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

}
