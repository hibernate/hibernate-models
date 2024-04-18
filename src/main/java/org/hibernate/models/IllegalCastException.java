/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

import org.hibernate.models.spi.AnnotationTarget;

/**
 * Indicates an illegal cast through one of the safe cast methods
 * such as {@linkplain AnnotationTarget#asClassDetails()},
 * {@linkplain AnnotationTarget#asMemberDetails()}, etc.
 *
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
