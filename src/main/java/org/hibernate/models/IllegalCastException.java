/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

/**
 * @author Steve Ebersole
 */
public class IllegalCastException extends ModelsException {
	public IllegalCastException(String message) {
		super( message );
	}

	public IllegalCastException(String message, Throwable cause) {
		super( message, cause );
	}
}
