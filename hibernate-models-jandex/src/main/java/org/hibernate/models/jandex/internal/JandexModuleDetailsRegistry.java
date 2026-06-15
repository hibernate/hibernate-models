/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.internal.ModuleDetailsRegistryStandard;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ModuleDetails;

import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.ModuleInfo;

/// Jandex module details registry.
///
/// Resolves modules from the Jandex index when `module-info.class` was
/// indexed, falling back to the standard JDK-backed resolution otherwise.
///
/// @since 1.3
/// @author Steve Ebersole
public class JandexModuleDetailsRegistry extends ModuleDetailsRegistryStandard {
	private final IndexView jandexIndex;
	private final ModelsContext context;

	/// Constructs a registry bound to the given Jandex index and models context.
	///
	/// @param jandexIndex The Jandex index
	/// @param context The owning models context
	public JandexModuleDetailsRegistry(IndexView jandexIndex, ModelsContext context) {
		super( context );
		this.jandexIndex = jandexIndex;
		this.context = context;
	}

	@Override
	protected ModuleDetails createModuleDetails(String name) {
		final ModuleInfo moduleInfo = jandexIndex.getModuleByName( DotName.createSimple( name ) );
		if ( moduleInfo != null ) {
			return new JandexModuleDetails( moduleInfo, context );
		}
		return super.createModuleDetails( name );
	}
}
