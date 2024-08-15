/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

/**
 * Indicates an attempt to access an unknown class by name
 *
 * @see org.hibernate.models.spi.ClassDetailsRegistry#getClassDetails
 * @see org.hibernate.models.spi.ClassLoading#classForName
 *
 * @author Steve Ebersole
 */
public class UnknownClassException extends ModelsException {
	public UnknownClassException(String message) {
		super( message );
	}

	public UnknownClassException(String message, Throwable cause) {
		super( message, cause );
	}
}
