/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassDetailsRegistry;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractClassDetailsRegistry implements ClassDetailsRegistry {
	protected final Map<String, ClassDetails> classDetailsMap;

	// subtype per type
	protected final Map<String, List<ClassDetails>> subTypeClassDetailsMap;

	protected AbstractClassDetailsRegistry() {
		this( new ConcurrentHashMap<>(), new ConcurrentHashMap<>() );
	}

	protected AbstractClassDetailsRegistry(
			Map<String, ClassDetails> classDetailsMap,
			Map<String, List<ClassDetails>> subTypeClassDetailsMap) {
		this.classDetailsMap = classDetailsMap;
		this.subTypeClassDetailsMap = subTypeClassDetailsMap;
	}

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
	public ClassDetails resolveClassDetails(String name, ClassDetailsBuilder creator) {
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

	protected abstract ClassDetails createClassDetails(String name, ClassDetailsBuilder creator);

	@Override public ClassDetails resolveClassDetails(
			String name,
			ClassDetailsCreator creator) {
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

	protected abstract ClassDetails createClassDetails(String name, ClassDetailsCreator creator);
}
