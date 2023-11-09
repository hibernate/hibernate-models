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
	 * Get an attribute descriptor by name
	 */
	<V> AttributeDescriptor<V> getAttribute(String name);
}
