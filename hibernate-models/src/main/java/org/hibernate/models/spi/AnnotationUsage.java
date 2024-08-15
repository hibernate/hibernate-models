/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

/**
 * Describes the usage of an {@linkplain AnnotationDescriptor annotation class} on one of its
 * allowable {@linkplain AnnotationTarget targets} where the source of the data is read from
 * something other than Java reflection.  In other words, an abstraction of an annotation usage
 * not read via {@linkplain java.lang.annotation.Annotation}.
 * <p/>
 * The standard way to access values is using {@linkplain #getAttributeValue}.
 *
 * @author Steve Ebersole
 */
public interface AnnotationUsage<A extends Annotation> {
	/**
	 * Descriptor for the type of the used annotation
	 */
	AnnotationDescriptor<A> getAnnotationDescriptor();

	/**
	 * Type of the used annotation
	 */
	default Class<A> getAnnotationType() {
		return getAnnotationDescriptor().getAnnotationType();
	}

	/**
	 * The target where this usage occurs
	 */
	AnnotationTarget getAnnotationTarget();

	/**
	 * Create an Annotation representation of this usage
	 */
	A toAnnotation();

	/**
	 * The value of the named annotation attribute
	 */
	<V> V findAttributeValue(String name);

	/**
	 * The value of the named annotation attribute
	 */
	default <V> V getAttributeValue(String name) {
		final Object value = findAttributeValue( name );
		if ( value == null ) {
			// this is unusual.  make sure the attribute exists.
			//		NOTE : the call to #getAttribute throws the exception if it does not
			getAnnotationDescriptor().getAttribute( name );
		}
		//noinspection unchecked
		return (V) value;
	}
}
