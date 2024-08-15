/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

/**
 * Registry of {@linkplain AnnotationDescriptor descriptors} for all known annotations
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptorRegistry {
	/**
	 * Get the descriptor for the given annotation {@code type}, creating one if needed.
	 */
	<A extends Annotation> AnnotationDescriptor<A> getDescriptor(Class<A> javaType);
	/**
	 * Get the descriptor for the given annotation {@code type}, creating one via the creator if needed.
	 */
	<A extends Annotation> AnnotationDescriptor<A> resolveDescriptor(Class<A> javaType, DescriptorCreator<A> creator);

	/**
	 * Assuming the {@code descriptor} is a {@linkplain AnnotationDescriptor#getRepeatableContainer() repeatable container},
	 * return the descriptor of the annotation for which it acts as a container.
	 */
	<A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(AnnotationDescriptor<A> descriptor);

	/**
	 * Shorthand for {@code getRepeatableDescriptor( getDescriptor( javaType ) )}
	 */
	<A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(Class<A> javaType);

	@FunctionalInterface
	interface DescriptorCreator<A extends Annotation> {
		AnnotationDescriptor<A> createDescriptor(Class<A> annotationType);
	}
}
