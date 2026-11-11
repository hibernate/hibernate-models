/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import jakarta.annotation.Nullable;

/// Indicates an attempt to access an unknown module by name.
///
/// @since 1.3
/// @author Steve Ebersole
public class UnknownModuleException extends ModelsException {
	public UnknownModuleException(@Nullable String message) {
		super( message );
	}
}
