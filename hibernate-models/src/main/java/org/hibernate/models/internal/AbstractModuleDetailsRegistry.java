/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.UnknownModuleException;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ModuleDetails;
import org.hibernate.models.spi.ModuleDetailsRegistry;

/// Basic support for module details registries.
///
/// Handles caching and lookup by module name, while allowing subclasses to
/// decide how a [ModuleDetails] is created for their backing metadata
/// source.
///
/// @since 1.3
/// @author Steve Ebersole
public abstract class AbstractModuleDetailsRegistry implements ModuleDetailsRegistry {
	protected final ModelsContext context;
	protected final Map<String, ModuleDetails> moduleDetailsMap = new HashMap<>();

	/// Constructs a registry bound to the given models context.
	///
	/// @param context The models context which owns this registry
	public AbstractModuleDetailsRegistry(ModelsContext context) {
		this.context = context;
	}

	@Override
	public ModuleDetails resolveModuleDetails(String name) {
		final ModuleDetails existing = findModuleDetails( name );
		if ( existing != null ) {
			return existing;
		}

		final ModuleDetails created = createModuleDetails( name );
		moduleDetailsMap.put( name, created );
		return created;
	}

	@Override
	public ModuleDetails resolveModuleDetails(Module module) {
		final String moduleName = module.getName();
		if ( moduleName == null ) {
			throw new UnknownModuleException( "Unable to resolve ModuleDetails for an unnamed module" );
		}
		final ModuleDetails existing = findModuleDetails( moduleName );
		if ( existing != null ) {
			return existing;
		}

		final ModuleDetails created = createModuleDetails( module );
		moduleDetailsMap.put( moduleName, created );
		return created;
	}

	@Override
	public ModuleDetails findModuleDetails(String name) {
		return moduleDetailsMap.get( name );
	}

	protected ModuleDetails createModuleDetails(String name) {
		final Module module = ModuleLayer.boot().findModule( name ).orElse( null );
		if ( module != null ) {
			return createModuleDetails( module );
		}

		throw new UnknownModuleException( "Unable to resolve ModuleDetails for `" + name + "`" );
	}

	/// Create module details from a loaded Java module.
	///
	/// @param module The Java module
	///
	/// @return The created module details
	protected abstract ModuleDetails createModuleDetails(Module module);
}
