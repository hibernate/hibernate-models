/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AnnotationUsage;

/**
 * Specialization of AnnotationUsage allowing mutation of its attribute values
 *
 * @author Steve Ebersole
 */
public interface MutableAnnotationUsage<A extends Annotation> extends AnnotationUsage<A> {
	/**
	 * Set the {@code value} of the named attribute
	 */
	@SuppressWarnings("UnusedReturnValue")
	<V> V setAttributeValue(String name, V value);
}
