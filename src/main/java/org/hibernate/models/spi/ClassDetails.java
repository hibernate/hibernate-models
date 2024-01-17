/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.List;
import java.util.function.Predicate;

import org.hibernate.models.internal.util.IndexedConsumer;
import org.hibernate.models.internal.ClassDetailsHelper;

/**
 * Abstraction for what Hibernate understands about a "class", generally before it has access to
 * the actual {@link Class} reference, if there is a {@code Class} at all (dynamic models).
 *
 * @see ClassDetailsRegistry
 *
 * @author Steve Ebersole
 */
public interface ClassDetails extends AnnotationTarget {
	/**
	 * The name of the class.
	 * <p/>
	 * Generally this is the same as the {@linkplain #getClassName() class name}.
	 * But in the case of Hibernate's {@code entity-name} feature, this would
	 * be the {@code entity-name}
	 */
	String getName();

	/**
	 * The name of the {@link Class}, or {@code null} for dynamic models.
	 *
	 * @apiNote Will be {@code null} for dynamic models
	 */
	String getClassName();

	@Override
	default Kind getKind() {
		return Kind.CLASS;
	}

	/**
	 * Whether the {@linkplain Class}, if one, represented by this ClassDetails is
	 * already loaded on the {@linkplain ClassLoader} used for {@linkplain ClassLoading loading}.
	 *
	 * @return {@code true} when there is a physical backing class, and it is loaded; {@code false} otherwise.
	 */
	boolean isResolved();

	/**
	 * Whether the class should be considered abstract.
	 */
	boolean isAbstract();

	/**
	 * Where the class is a Java record
	 */
	boolean isRecord();

	/**
	 * Details for the class that is the super type for this class.
	 */
	ClassDetails getSuperType();

	/**
	 * Details for the interfaces this class implements.
	 */
	List<ClassDetails> getImplementedInterfaceTypes();

	/**
	 * Whether the described class is an implementor of the given {@code checkType}.
	 */
	default boolean isImplementor(Class<?> checkType) {
		return ClassDetailsHelper.isImplementor( checkType, this );
	}

	/**
	 * Get the fields for this class
	 */
	List<FieldDetails> getFields();

	/**
	 * Visit each field
	 */
	void forEachField(IndexedConsumer<FieldDetails> consumer);

	/**
	 * Find a field by check
	 */
	default FieldDetails findField(Predicate<FieldDetails> check) {
		final List<FieldDetails> fields = getFields();
		for ( int i = 0; i < fields.size(); i++ ) {
			final FieldDetails fieldDetails = fields.get( i );
			if ( check.test( fieldDetails ) ) {
				return fieldDetails;
			}
		}
		return null;
	}

	/**
	 * Find a field by name
	 */
	default FieldDetails findFieldByName(String name) {
		assert name != null;
		return findField( fieldDetails -> name.equals( fieldDetails.getName() ) );
	}

	/**
	 * Get the methods for this class
	 */
	List<MethodDetails> getMethods();

	/**
	 * Visit each method
	 */
	void forEachMethod(IndexedConsumer<MethodDetails> consumer);

	/**
	 * Get the record components for this class
	 */
	List<RecordComponentDetails> getRecordComponents();

	/**
	 * Find a record component by check
	 */
	default RecordComponentDetails findRecordComponent(Predicate<RecordComponentDetails> check) {
		final List<RecordComponentDetails> components = getRecordComponents();
		for ( int i = 0; i < components.size(); i++ ) {
			final RecordComponentDetails component = components.get( i );
			if ( check.test( component ) ) {
				return component;
			}
		}
		return null;
	}

	/**
	 * Find a record component by name
	 */
	default RecordComponentDetails findRecordComponentByName(String name) {
		assert name != null;
		return findRecordComponent( component -> name.equals( component.getName() ) );
	}

	/**
	 * Know what you are doing before calling this method
	 */
	<X> Class<X> toJavaClass();
}
