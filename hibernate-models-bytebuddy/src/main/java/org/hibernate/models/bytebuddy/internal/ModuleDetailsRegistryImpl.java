/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import org.hibernate.models.internal.ModuleDetailsRegistryStandard;
import org.hibernate.models.spi.ModuleDetails;

import net.bytebuddy.description.module.ModuleDescription;

/// Byte Buddy module details registry.
///
/// Creates module details backed by Byte Buddy's loaded-module description
/// support.
///
/// @since 1.3
/// @author Steve Ebersole
public class ModuleDetailsRegistryImpl extends ModuleDetailsRegistryStandard {
	private final ByteBuddyModelsContextImpl context;

	/// Constructs a registry bound to the given Byte Buddy models context.
	///
	/// @param context The owning Byte Buddy models context
	public ModuleDetailsRegistryImpl(ByteBuddyModelsContextImpl context) {
		super( context );
		this.context = context;
	}

	@Override
	protected ModuleDetails createModuleDetails(Module module) {
		return new ModuleDetailsImpl( ModuleDescription.ForLoadedModule.of( module ), context );
	}
}
