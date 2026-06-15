/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

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
	ModuleDetails resolveModuleDetails(String name);

	/// Resolves the details for a loaded Java module.
	///
	/// @param module The Java module
	///
	/// @return The resolved module details
	ModuleDetails resolveModuleDetails(Module module);

	/// Find the module with the given `name`, if there is one.
	/// Returns `null` if there are none registered with that name.
	///
	/// @param name The module name
	///
	/// @return The registered module details, or `null`
	ModuleDetails findModuleDetails(String name);

	@SuppressWarnings("unchecked")
	default <S> S as(Class<S> type) {
		if ( type.isInstance( this ) ) {
			return (S) this;
		}
		throw new UnsupportedOperationException( "Unsure how to cast " + this + " to " + type.getName() );
	}
}
