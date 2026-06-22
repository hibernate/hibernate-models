/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.EnumSet;

import org.hibernate.models.CompleteAnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;

/**
 * Specialized AnnotationDescriptor implementation intended for use in describing
 * Hibernate and JPA annotations.  Note especially that this implementation
 * does not collect annotations from the annotation class as we never care about
 * meta-annotations in these cases.
 *
 * @deprecated Deprecated for JPMS purposes.  Use {@linkplain CompleteAnnotationDescriptor} instead.
 *
 * @author Steve Ebersole
 */
@Deprecated(since = "1.3", forRemoval = true)
public class OrmAnnotationDescriptor<A extends Annotation, C extends A>
		extends CompleteAnnotationDescriptor<A, C> {

	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<C> concreteClass,
			EnumSet<AnnotationTarget.Kind> allowableTargets,
			boolean inherited) {
		this( annotationType, concreteClass, allowableTargets, inherited, null );
	}

	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<C> concreteClass,
			EnumSet<AnnotationTarget.Kind> allowableTargets,
			boolean inherited,
			AnnotationDescriptor<?> repeatableContainer) {
		super( annotationType, concreteClass, allowableTargets, inherited, repeatableContainer );
	}

	/**
	 * @deprecated use {@link #OrmAnnotationDescriptor(Class, Class, EnumSet, boolean)} instead
	 */
	@Deprecated(forRemoval = true)
	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<C> concreteClass) {
		this( annotationType, concreteClass, null );
	}

	/**
	 * @deprecated use {@link #OrmAnnotationDescriptor(Class, Class, EnumSet, boolean, AnnotationDescriptor)} instead
	 */
	@Deprecated(forRemoval = true)
	public OrmAnnotationDescriptor(
			Class<A> annotationType,
			Class<C> concreteClass,
			AnnotationDescriptor<?> repeatableContainer) {
		this(annotationType, concreteClass, AnnotationHelper.extractTargets( annotationType ), AnnotationHelper.isInherited( annotationType ), repeatableContainer);
	}
}
