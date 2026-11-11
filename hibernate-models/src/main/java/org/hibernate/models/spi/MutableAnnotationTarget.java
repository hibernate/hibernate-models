/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import jakarta.annotation.Nonnull;

/**
 * Extension of AnnotationTarget which allows manipulation of the annotations
 *
 * @author Steve Ebersole
 */
public interface MutableAnnotationTarget extends AnnotationTarget {
	/**
	 * Removes all annotation usages currently associated with this target.
	 * Useful for complete XML mappings.
	 */
	void clearAnnotationUsages();

	/**
	 * Add an annotation usage to this target
	 */
	<X extends Annotation> void addAnnotationUsage(@Nonnull X annotationUsage);

	/**
	 * Remove an annotation, by type, from this target if there is such a usage.
	 */
	<X extends Annotation> void removeAnnotationUsage(@Nonnull AnnotationDescriptor<X> annotationType);

	/**
	 * Applies a usage of the given {@code annotationType} to this target.  Will return
	 * an existing usage, if one, or create a new usage.
	 */
	@Nonnull
	default <A extends Annotation> A applyAnnotationUsage(
			@Nonnull AnnotationDescriptor<A> annotationType,
			@Nonnull ModelsContext modelContext) {
		final A existing = getAnnotationUsage( annotationType, modelContext );
		if ( existing != null ) {
			return existing;
		}

		final A usage = annotationType.createUsage( modelContext );
		addAnnotationUsage( usage );
		return usage;
	}

	/**
	 * Creates and replaces (if any) an existing usage of the given annotation.
	 * <p/>
	 * For repeatable annotations, use {@linkplain #replaceAnnotationUsage(AnnotationDescriptor, AnnotationDescriptor, ModelsContext)}
	 * instead.
	 *
	 * @apiNote Generally replacement is used with XML processing and, again generally,
	 * only for repeatable annotations using
	 * {@linkplain #replaceAnnotationUsage(AnnotationDescriptor, AnnotationDescriptor, ModelsContext)}
	 *
	 * @see #replaceAnnotationUsage(AnnotationDescriptor, AnnotationDescriptor, ModelsContext)
	 */
	@Nonnull
	default <A extends Annotation> A replaceAnnotationUsage(
			@Nonnull AnnotationDescriptor<A> annotationType,
			@Nonnull ModelsContext modelContext) {
		final A usage = annotationType.createUsage( modelContext );
		// effectively overwrites any previous registration
		addAnnotationUsage( usage );
		return usage;
	}

	/**
	 * Creates and replaces (if any) an existing usage of the given annotation.
	 * <p/>
	 * For repeatable annotations, use
	 * Applies a usage of the given {@code annotationType} to this target.  Will return
	 * an existing usage, if one, or create a new usage.
	 *
	 * @see #replaceAnnotationUsage(AnnotationDescriptor, ModelsContext)
	 */
	@Nonnull
	<S extends Annotation, P extends Annotation> P replaceAnnotationUsage(
			@Nonnull AnnotationDescriptor<S> repeatableType,
			@Nonnull AnnotationDescriptor<P> containerType,
			@Nonnull ModelsContext modelContext);

	@Nonnull
	@Override
	MutableClassDetails asClassDetails();

	@Nonnull
	@Override
	MutableMemberDetails asMemberDetails();
}
