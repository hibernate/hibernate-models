/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import org.hibernate.models.spi.AnnotationTarget;

/**
 * Indicates a problem accessing annotation details from the domain model sources.
 *
 * @see AnnotationTarget#getAnnotationUsage
 *
 * @author Steve Ebersole
 */
public class AnnotationAccessException extends ModelsException {
	public AnnotationAccessException(String message) {
		super( message );
	}

	public AnnotationAccessException(String message, Throwable cause) {
		super( message, cause );
	}
}
