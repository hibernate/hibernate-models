/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.models.UnknownAnnotationAttributeException;

/**
 * Describes an annotation type (the Class)
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptor<A extends Annotation> extends AnnotationTarget {
	@Override
	default Kind getKind() {
		return Kind.ANNOTATION;
	}

	/**
	 * The annotation type
	 */
	Class<A> getAnnotationType();

	/**
	 * The places the described annotation can be used
	 */
	EnumSet<Kind> getAllowableTargets();

	/**
	 * Whether the annotation defined as {@linkplain java.lang.annotation.Inherited inherited}
	 */
	boolean isInherited();

	/**
	 * Whether the annotation is {@linkplain Repeatable repeatable}.
	 */
	default boolean isRepeatable() {
		return getRepeatableContainer() != null;
	}

	/**
	 * If the described annotation is {@linkplain #isRepeatable repeatable}, returns the descriptor
	 * for the {@linkplain Repeatable#value() container} annotation.
	 */
	AnnotationDescriptor<?> getRepeatableContainer();

	/**
	 * The attributes of the annotation
	 */
	List<AttributeDescriptor<?>> getAttributes();

	/**
	 * Get an attribute descriptor by name.
	 *
	 * @throws UnknownAnnotationAttributeException if the name is not an
	 * attribute of the described annotation.
	 */
	<V> AttributeDescriptor<V> findAttribute(String name);

	/**
	 * Get an attribute descriptor by name, returning {@code null} if the name
	 * is not an attribute of the described annotation.
	 */
	default <V> AttributeDescriptor<V> getAttribute(String name) {
		final AttributeDescriptor<Object> attribute = findAttribute( name );
		if ( attribute != null ) {
			//noinspection unchecked
			return (AttributeDescriptor<V>) attribute;
		}

		throw new UnknownAnnotationAttributeException( getAnnotationType(), name );
	}
}
