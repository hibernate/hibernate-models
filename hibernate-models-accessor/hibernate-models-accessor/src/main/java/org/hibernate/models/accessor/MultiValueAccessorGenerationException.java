/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

/**
 * Thrown when a multi-value accessor (reader or writer) cannot be generated
 * for a given class and member set. Consumers should catch this to fall back
 * to per-property access.
 */
public class MultiValueAccessorGenerationException extends HibernateAccessorException {

	public MultiValueAccessorGenerationException(String message) {
		super( message );
	}

	public MultiValueAccessorGenerationException(String message, Throwable cause) {
		super( message, cause );
	}
}
