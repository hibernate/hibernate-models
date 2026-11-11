/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import java.lang.annotation.Annotation;
import java.util.Locale;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Indicates an attempt to access a non-existent annotation attribute
 *
 * @see org.hibernate.models.spi.AnnotationDescriptor#getAttribute
 *
 * @author Steve Ebersole
 */
public class UnknownAnnotationAttributeException extends ModelsException {
	public UnknownAnnotationAttributeException(@Nonnull Class<? extends Annotation> annotationType, @Nonnull String attributeName) {
		this( String.format(
				Locale.ROOT,
				"Unable to locate attribute named `%s` on annotation `%s`",
				attributeName,
				annotationType.getName()
		) );
	}

	public UnknownAnnotationAttributeException(@Nullable String message) {
		super( message );
	}
}
