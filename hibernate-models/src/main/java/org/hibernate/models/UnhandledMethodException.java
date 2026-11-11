/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import jakarta.annotation.Nullable;

/**
 * Generally indicates an attempt to call an unknown/unhandled method on an annotation
 * {@linkplain org.hibernate.models.internal.AnnotationProxy proxy}.
 *
 * @author Steve Ebersole
 */
public class UnhandledMethodException extends ModelsException {
	public UnhandledMethodException(@Nullable String message) {
		super( message );
	}

	public UnhandledMethodException(@Nullable String message, @Nullable Throwable cause) {
		super( message, cause );
	}
}
