/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import org.hibernate.models.Incubating;
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
	 * Whether this registry is tracking interface implementors.
	 *
	 * @see org.hibernate.models.Settings#TRACK_IMPLEMENTORS
	 */
	boolean isTrackingImplementors();

	/**
	 * Resolves a managed-class by name.  If there is currently no such registration,
	 * one is created.
	 */
	ClassDetails resolveClassDetails(String name);

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
	 * Get the list of all direct subtypes for the named managed-class.
	 *
	 * @deprecated Use {@linkplain #getDirectSubtypes(String)} instead.
	 */
	@Deprecated
	List<ClassDetails> getDirectSubTypes(String superTypeName);

	/**
	 * Get the list of all direct subtypes for the named managed-class.
	 */
	Set<ClassDetails> getDirectSubtypes(String superTypeName);

	/**
	 * Visit each direct subtype of the named managed-class
	 *
	 * @deprecated Use {@linkplain #forEachDirectSubtype} instead.
	 */
	@Deprecated
	default void forEachDirectSubType(String typeName, ClassDetailsConsumer consumer) {
		forEachDirectSubtype( typeName, consumer );
	}

	/**
	 * Visit each direct subtype of the named managed-class
	 */
	void forEachDirectSubtype(String typeName, ClassDetailsConsumer consumer);

	/**
	 * Get the list of all direct implementors, including specializations, for the named interface.
	 *
	 * @return The direct implementors of the named interface.
	 *
	 * @see #isTrackingImplementors
	 *
	 * @apiNote Does not verify that {@code interfaceName} actually names an interface.
	 */
	@Incubating
	Set<ClassDetails> getDirectImplementors(String interfaceName);

	/**
	 * Visit each direct implementor, including specializations, of the named interface.
	 *
	 * @see #isTrackingImplementors
	 *
	 * @apiNote Does not verify that {@code interfaceName} actually names an
	 * interface.  If it does not, no callbacks will happen.
	 */
	@Incubating
	void forEachDirectImplementor(String interfaceName, ClassDetailsConsumer consumer);

	/**
	 * Find all non-abstract ClassDetails which extended from or implements the named
	 * base type, which might be a class or interface.
	 *
	 * @param base The name of the class/interface from which to start walking.
	 *
	 * @apiNote If {@code base} is a concrete type, it will also be returned.
	 * @see #findConcreteTypes(String, boolean)
	 * @see #isTrackingImplementors
	 */
	@Incubating
	default Set<ClassDetails> findConcreteTypes(String base) {
		return findConcreteTypes( base, true );
	}

	/**
	 * Find ClassDetails for all non-abstract subtypes / implementors
	 * of the given base (which might be a class or interface).
	 *
	 * @param base The name of the class/interface from which to start walking.
	 * @param includeBase Whether to include {@code base} if it is concrete type.
	 *
	 * @see #getDirectSubTypes
	 * @see #getDirectImplementors
	 */
	@Incubating
	default Set<ClassDetails> findConcreteTypes(String base, boolean includeBase) {
		final Set<ClassDetails> result = new LinkedHashSet<>();
		walkConcreteTypes( base, includeBase, result::add );
		return result;
	}

	/**
	 * Visit all concrete (non-abstract, non-interface) ClassDetails from the give base type.
	 *
	 * @param base The name of the class/interface from which to start walking.
	 * @param includeBase Whether to include {@code base} if it is concrete type.
	 * @param consumer The callback for each concrete ClassDetails
	 */
	@Incubating
	void walkConcreteTypes(String base, boolean includeBase, ClassDetailsConsumer consumer);

	/**
	 * Walks the inheritance tree downward, starting from {@code base},
	 * calling the consumer for each subclass and interface which is directly
	 * an implementor.
	 *
	 * @param base The type from which to start.
	 * @param includeBase Whether to include {@code base} if it is concrete type.
	 * @param consumer The callback.
	 *
	 * @see #isTrackingImplementors
	 */
	@Incubating
	void walkImplementors(String base, boolean includeBase, ClassDetailsConsumer consumer);

	/**
	 * Walks the inheritance tree, starting from {@code base}, "downward"
	 * calling the consumer for each subclass and interface which is directly
	 * an implementor.
	 *
	 * @param base The type from which to start.
	 * @param includeBase Whether to include {@code base} if it is concrete type.
	 *
	 * @see #isTrackingImplementors
	 */
	@Incubating
	default Set<ClassDetails> collectImplementors(String base, boolean includeBase) {
		return collectImplementors( base, includeBase, null );
	}

	/**
	 * Walks the inheritance tree, starting from {@code base}, "downward"
	 * calling the consumer for each subclass and interface which is directly
	 * an implementor.
	 *
	 * @param base The type from which to start.
	 * @param includeBase Whether to include {@code base} if it is concrete type.
	 * @param exclusions Exclusive check to filter ClassDetails out of the result.
	 *
	 * @see #isTrackingImplementors
	 */
	@Incubating
	default Set<ClassDetails> collectImplementors(
			String base,
			boolean includeBase,
			Predicate<ClassDetails> exclusions) {
		final Set<ClassDetails> result = new LinkedHashSet<>();
		walkImplementors( base, includeBase, classDetails -> {
			if ( exclusions == null || !exclusions.test( classDetails ) ) {
				result.add( classDetails );
			}
		} );
		return result;
	}

	/**
	 * Access to the ClassDetailsBuilder used in this registry
	 */
	ClassDetailsBuilder getClassDetailsBuilder();

	@SuppressWarnings("unchecked")
	default <S> S as(Class<S> type) {
		if ( type.isInstance( this ) ) {
			return (S) this;
		}
		throw new UnsupportedOperationException( "Unsure how to cast " + this + " to " + type.getName() );
	}

	@FunctionalInterface
	interface ClassDetailsConsumer {
		void consume(ClassDetails classDetails);
	}
}
