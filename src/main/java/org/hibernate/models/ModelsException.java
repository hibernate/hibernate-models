/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

/**
 * Base exception for models building
 *
 * @author Steve Ebersole
 */
public class ModelsException extends RuntimeException {
	public ModelsException(String message) {
		super( message );
	}

	public ModelsException(String message, Throwable cause) {
		super( message, cause );
	}
}
