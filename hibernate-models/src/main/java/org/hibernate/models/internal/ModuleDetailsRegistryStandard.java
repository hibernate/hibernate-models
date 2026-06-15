/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jdk.JdkModuleDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ModuleDetails;

/// Standard JDK-based module details registry implementation.
///
/// Uses the JDK [Module] API to read module annotations.
///
/// @since 1.3
/// @author Steve Ebersole
public class ModuleDetailsRegistryStandard extends AbstractModuleDetailsRegistry {
	/// Constructs a registry bound to the given models context.
	///
	/// @param context The models context which owns this registry
	public ModuleDetailsRegistryStandard(ModelsContext context) {
		super( context );
	}

	@Override
	protected ModuleDetails createModuleDetails(Module module) {
		return new JdkModuleDetails( module, context );
	}
}
