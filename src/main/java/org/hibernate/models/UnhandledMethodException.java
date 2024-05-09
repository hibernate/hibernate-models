/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

/**
 * Generally indicates an attempt to call an unknown/unhandled method on an annotation
 * {@linkplain org.hibernate.models.internal.AnnotationProxy proxy}.
 *
 * @author Steve Ebersole
 */
public class UnhandledMethodException extends ModelsException {
	public UnhandledMethodException(String message) {
		super( message );
	}

	public UnhandledMethodException(String message, Throwable cause) {
		super( message, cause );
	}
}
