/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import jakarta.annotation.Nullable;

/**
 * Indicates an attempt to access an unknown class by name
 *
 * @see org.hibernate.models.spi.ClassDetailsRegistry#getClassDetails
 * @see org.hibernate.models.spi.ClassLoading#classForName
 *
 * @author Steve Ebersole
 */
public class UnknownClassException extends ModelsException {
	public UnknownClassException(@Nullable String message) {
		super( message );
	}

	public UnknownClassException(@Nullable String message, @Nullable Throwable cause) {
		super( message, cause );
	}
}
