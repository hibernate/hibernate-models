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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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

	public static <A extends Annotation> boolean isInherited(@Nonnull Class<A> annotationType) {
		return annotationType.isAnnotationPresent( Inherited.class );
	}

	@Nonnull
	public static <A extends Annotation> EnumSet<AnnotationTarget.Kind> extractTargets(@Nonnull Class<A> annotationType) {
		return AnnotationTarget.Kind.from( annotationType.getAnnotation( Target.class ) );
	}

	@Nullable
	public static <A extends Annotation, R> R extractValue(@Nonnull A annotationUsage, @Nonnull AttributeDescriptor<R> attributeDescriptor) {
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
