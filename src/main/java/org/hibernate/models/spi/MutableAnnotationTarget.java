/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

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
	<X extends Annotation> void addAnnotationUsage(AnnotationUsage<X> annotationUsage);

	/**
	 * Applies a usage of the given {@code annotationType} to this target.  Will return
	 * an existing usage, if one, or create a new usage.
	 */
	default <A extends Annotation> MutableAnnotationUsage<A> applyAnnotationUsage(
			AnnotationDescriptor<A> annotationType,
			SourceModelBuildingContext buildingContext) {
		final MutableAnnotationUsage<A> existing = (MutableAnnotationUsage<A>) getAnnotationUsage( annotationType );
		if ( existing != null ) {
			return existing;
		}

		final MutableAnnotationUsage<A> usage = annotationType.createUsage( buildingContext );
		addAnnotationUsage( usage );
		return usage;
	}

	@Override
	MutableClassDetails asClassDetails();

	@Override
	MutableMemberDetails asMemberDetails();
}
