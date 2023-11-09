/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.util.EnumSet;

import org.hibernate.models.internal.jdk.AnnotationDescriptorImpl;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;

/**
 * Helper for dealing with actual {@link Annotation} references
 *
 * @author Steve Ebersole
 */
public class AnnotationHelper {
	private AnnotationHelper() {
		// disallow direct instantiation
	}

	public static <A extends Annotation> boolean isInherited(Class<A> annotationType) {
		return annotationType.isAnnotationPresent( Inherited.class );
	}

	public static <A extends Annotation> EnumSet<AnnotationTarget.Kind> extractTargets(Class<A> annotationType) {
		return AnnotationTarget.Kind.from( annotationType.getAnnotation( Target.class ) );
	}

	public static <A extends Annotation> AnnotationDescriptor<A> createOrmDescriptor(Class<A> javaType) {
		return createOrmDescriptor( javaType, null );
	}

	public static <A extends Annotation> AnnotationDescriptor<A> createOrmDescriptor(
			Class<A> javaType,
			AnnotationDescriptor<?> repeatableContainer) {
		assert javaType != null;
		return new AnnotationDescriptorImpl<>( javaType, repeatableContainer );
	}
}
