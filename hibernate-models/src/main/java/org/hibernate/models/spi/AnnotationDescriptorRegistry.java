/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Registry of {@linkplain AnnotationDescriptor descriptors} for all known annotations
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptorRegistry {
	/**
	 * Get the descriptor for the given annotation {@code type}, creating one if needed.
	 */
	@Nonnull
	<A extends Annotation> AnnotationDescriptor<A> getDescriptor(@Nonnull Class<A> javaType);
	/**
	 * Get the descriptor for the given annotation {@code type}, creating one via the creator if needed.
	 */
	@Nonnull
	<A extends Annotation> AnnotationDescriptor<A> resolveDescriptor(@Nonnull Class<A> javaType, @Nonnull DescriptorCreator<A> creator);

	/**
	 * Assuming the {@code descriptor} is a {@linkplain AnnotationDescriptor#getRepeatableContainer() repeatable container},
	 * return the descriptor of the annotation for which it acts as a container.
	 */
	@Nullable
	<A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(@Nonnull AnnotationDescriptor<A> descriptor);

	/**
	 * Shorthand for {@code getRepeatableDescriptor( getDescriptor( javaType ) )}
	 */
	@Nullable
	<A extends Annotation> AnnotationDescriptor<A> getContainedRepeatableDescriptor(@Nonnull Class<A> javaType);

	@FunctionalInterface
	interface DescriptorCreator<A extends Annotation> {
		@Nonnull
		AnnotationDescriptor<A> createDescriptor(@Nonnull Class<A> annotationType);
	}
}
