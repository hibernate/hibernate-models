/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumSet;
import java.util.Locale;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AttributeDescriptor;

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

	public static <A extends Annotation, R> R extractValue(A annotationUsage, AttributeDescriptor<R> attributeDescriptor) {
		try {
			//noinspection unchecked
			return (R) attributeDescriptor.getAttributeMethod().invoke( annotationUsage );
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new AnnotationAccessException(
					String.format(
							Locale.ROOT,
							"Unable to access annotation attribute value : %s.%s",
							annotationUsage.annotationType().getName(),
							attributeDescriptor.getName()
					),
					e
			);
		}
	}
}
