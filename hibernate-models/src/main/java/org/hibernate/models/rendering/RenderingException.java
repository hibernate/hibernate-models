/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.rendering;

import jakarta.annotation.Nullable;

import org.hibernate.models.ModelsException;

/**
 * Indicates a problem performing rendering.  Generally this indicates IO problems.
 *
 * @author Steve Ebersole
 */
public class RenderingException extends ModelsException {
	public RenderingException(@Nullable String message) {
		super( message );
	}

	public RenderingException(@Nullable String message, @Nullable Throwable cause) {
		super( message, cause );
	}
}
