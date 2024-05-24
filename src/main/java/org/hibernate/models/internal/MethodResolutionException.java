/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import org.hibernate.models.ModelsException;

/**
 * @author Steve Ebersole
 */
public class MethodResolutionException extends ModelsException {
	public MethodResolutionException(String message) {
		super( message );
	}

	public MethodResolutionException(String message, Throwable cause) {
		super( message, cause );
	}
}
