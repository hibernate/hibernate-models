/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import jakarta.annotation.Nullable;

/**
 * Base exception for hibernate-models
 *
 * @author Steve Ebersole
 */
public class ModelsException extends RuntimeException {
	public ModelsException(@Nullable String message) {
		super( message );
	}

	public ModelsException(@Nullable String message, @Nullable Throwable cause) {
		super( message, cause );
	}
}
