/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.util.HashMap;
import java.util.Map;

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

	private Map<String, String> packageToModuleMap;

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

	public ModuleDetails findModuleByPackage(String packageName) {
		final String moduleName = getPackageToModuleMap().get( packageName );
		if ( moduleName == null ) {
			return null;
		}
		return resolveModuleDetails( moduleName );
	}

	private Map<String, String> getPackageToModuleMap() {
		if ( packageToModuleMap == null ) {
			packageToModuleMap = buildPackageToModuleMap();
		}
		return packageToModuleMap;
	}

	private Map<String, String> buildPackageToModuleMap() {
		final Map<String, String> map = new HashMap<>();
		for ( ModuleInfo moduleInfo : jandexIndex.getKnownModules() ) {
			final String moduleName = moduleInfo.name().toString();
			for ( DotName pkg : moduleInfo.packages() ) {
				map.put( pkg.toString(), moduleName );
			}
		}
		return map;
	}
}
