/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import java.io.Serial;
import java.io.Serializable;

import jakarta.annotation.Nonnull;

/// Reference to an object stored in a [ModelsArchive] table.
///
/// @since 2.0
/// @author Steve Ebersole
public record ModelReference(@Nonnull Kind kind, int id) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public ModelReference {
		if ( kind == null ) {
			throw new IllegalArgumentException( "Model reference kind cannot be null" );
		}
		if ( id < 0 ) {
			throw new IllegalArgumentException( "Model reference id cannot be negative" );
		}
	}

	public enum Kind {
		@Nonnull
		CLASS,
		@Nonnull
		TYPE,
		@Nonnull
		FIELD,
		@Nonnull
		METHOD,
		@Nonnull
		CONSTRUCTOR,
		@Nonnull
		RECORD_COMPONENT,
		@Nonnull
		MODULE
	}
}
