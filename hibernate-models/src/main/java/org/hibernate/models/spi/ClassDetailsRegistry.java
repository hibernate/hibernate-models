/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.List;

import org.hibernate.models.UnknownClassException;

/**
 * Registry of all {@link ClassDetails} references
 *
 * @implSpec Quite a few methods here are marked to ignore the fact that they
 * are unused.  The expectation is that most of these methods are used by
 * consumers (Hibernate ORM, e.g.).  DO NOT REMOVE THEM!!!
 *
 * @author Steve Ebersole
 */
public interface ClassDetailsRegistry {
	/**
	 * Find the managed-class with the given {@code name}, if there is one.
	 * Returns {@code null} if there are none registered with that name.
	 */
	ClassDetails findClassDetails(String name);

	/**
	 * Form of {@link #findClassDetails} throwing an exception if no registration is found
	 *
	 * @throws UnknownClassException If no registration is found with the given {@code name}
	 */
	@SuppressWarnings("unused")
	default ClassDetails getClassDetails(String name) {
		final ClassDetails named = findClassDetails( name );
		if ( named == null ) {
			if ( "void".equals( name ) ) {
				return null;
			}
			throw new UnknownClassException( "Unknown managed class - " + name );
		}
		return named;
	}

	/**
	 * Visit each registered class details
	 */
	@SuppressWarnings("unused")
	void forEachClassDetails(ClassDetailsConsumer consumer);

	/**
	 * Get the list of all direct subtypes for the named managed-class.  Returns
	 * {@code null} if there are none
	 */
	List<ClassDetails> getDirectSubTypes(String superTypeName);

	/**
	 * Visit each direct subtype of the named managed-class
	 */
	@SuppressWarnings("unused")
	void forEachDirectSubType(String superTypeName, ClassDetailsConsumer consumer);

	/**
	 * Resolves a managed-class by name.  If there is currently no such registration,
	 * one is created.
	 */
	ClassDetails resolveClassDetails(String name);

	default <S> S as(Class<S> type) {
		//noinspection unchecked
		return (S) this;
	}

	@FunctionalInterface
	interface ClassDetailsConsumer {
		void consume(ClassDetails classDetails);
	}
}
