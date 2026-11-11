/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.UnknownModuleException;

/// Registry of [ModuleDetails] references.
///
/// Implementations are responsible for resolving module annotations using the
/// metadata source associated with the active [ModelsContext].
///
/// @since 1.3
/// @author Steve Ebersole
public interface ModuleDetailsRegistry {
	/// Resolves a module by name.  If there is currently no such registration,
	/// one is created.
	///
	/// @param name The module name
	///
	/// @return The resolved module details
	///
	/// @throws UnknownModuleException If the module cannot be resolved
	@Nonnull
	ModuleDetails resolveModuleDetails(@Nonnull String name);

	/// Resolves the details for a loaded Java module.
	///
	/// @param module The Java module
	///
	/// @return The resolved module details
	@Nonnull
	ModuleDetails resolveModuleDetails(@Nonnull Module module);

	/// Find the module with the given `name`, if there is one.
	/// Returns `null` if there are none registered with that name.
	///
	/// @param name The module name
	///
	/// @return The registered module details, or `null`
	@Nullable
	ModuleDetails findModuleDetails(@Nonnull String name);

	@Nonnull
	@SuppressWarnings("unchecked")
	default <S> S as(@Nonnull Class<S> type) {
		if ( type.isInstance( this ) ) {
			return (S) this;
		}
		throw new UnsupportedOperationException( "Unsure how to cast " + this + " to " + type.getName() );
	}
}
