/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import jakarta.annotation.Nullable;

import org.hibernate.models.spi.AnnotationTarget;

/**
 * Indicates a problem accessing annotation details from the domain model sources.
 *
 * @see AnnotationTarget#getAnnotationUsage
 *
 * @author Steve Ebersole
 */
public class AnnotationAccessException extends ModelsException {
	public AnnotationAccessException(@Nullable String message) {
		super( message );
	}

	public AnnotationAccessException(@Nullable String message, @Nullable Throwable cause) {
		super( message, cause );
	}
}
