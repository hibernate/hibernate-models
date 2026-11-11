/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import java.lang.annotation.Annotation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Indicates that a completed annotation usage does not satisfy the contract of
 * its annotation type.
 *
 * @since 2.0
 * @author Steve Ebersole
 */
public class InvalidAnnotationUsageException extends ModelsException {
	private final Class<? extends Annotation> annotationType;
	private final String attributePath;

	public InvalidAnnotationUsageException(
			@Nonnull Class<? extends Annotation> annotationType,
			@Nonnull String attributePath,
			@Nullable String message) {
		super( message + " [" + attributePath + "]" );
		this.annotationType = annotationType;
		this.attributePath = attributePath;
	}

	public InvalidAnnotationUsageException(
			@Nonnull Class<? extends Annotation> annotationType,
			@Nonnull String attributePath,
			@Nullable String message,
			@Nullable Throwable cause) {
		super( message + " [" + attributePath + "]", cause );
		this.annotationType = annotationType;
		this.attributePath = attributePath;
	}

	@Nonnull
	public Class<? extends Annotation> getAnnotationType() {
		return annotationType;
	}

	@Nonnull
	public String getAttributePath() {
		return attributePath;
	}
}
