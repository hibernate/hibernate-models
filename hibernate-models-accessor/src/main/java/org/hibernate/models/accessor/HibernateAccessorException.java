/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor;

/**
 * Unchecked exception thrown when an accessor operation fails.
 *
 * <p>This typically wraps lower-level reflection or invocation errors encountered
 * while creating or using accessors.
 */
public class HibernateAccessorException extends RuntimeException {

	/** Constructs a new exception with no detail message or cause. */
	public HibernateAccessorException() {
	}

	/**
	 * Constructs a new exception with the given detail message.
	 *
	 * @param message the detail message
	 */
	public HibernateAccessorException(String message) {
		super(message);
	}

	/**
	 * Constructs a new exception with the given detail message and cause.
	 *
	 * @param message the detail message
	 * @param cause the underlying cause
	 */
	public HibernateAccessorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the given cause.
	 *
	 * @param cause the underlying cause
	 */
	public HibernateAccessorException(Throwable cause) {
		super(cause);
	}
}
