/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import jakarta.annotation.Nullable;

import org.hibernate.models.spi.AnnotationTarget;

/**
 * Indicates an illegal cast through one of the safe cast methods
 * such as {@linkplain AnnotationTarget#asClassDetails()},
 * {@linkplain AnnotationTarget#asMemberDetails()}, etc.
 *
 * @author Steve Ebersole
 */
public class IllegalCastException extends ModelsException {
	public IllegalCastException(@Nullable String message) {
		super( message );
	}

	public IllegalCastException(@Nullable String message, @Nullable Throwable cause) {
		super( message, cause );
	}
}
