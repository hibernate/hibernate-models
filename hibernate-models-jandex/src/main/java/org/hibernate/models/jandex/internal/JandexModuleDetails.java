/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationTargetSupport;
import org.hibernate.models.internal.ModuleDetailsSupport;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.ModuleInfo;

/// ModuleDetails implementation based on Jandex module information.
///
/// Annotation usages are read lazily from the [ModuleInfo] annotations
/// indexed for `module-info.class`.
///
/// @since 1.3
/// @author Steve Ebersole
public class JandexModuleDetails implements ModuleDetailsSupport, AnnotationTargetSupport {
	private final ModuleInfo moduleInfo;
	private final ModelsContext modelContext;

	private Map<Class<? extends Annotation>, ? extends Annotation> usageMap;

	/// Constructs module details for indexed Jandex module information.
	///
	/// @param moduleInfo The Jandex module information
	/// @param modelContext The owning models context
	public JandexModuleDetails(ModuleInfo moduleInfo, ModelsContext modelContext) {
		this.moduleInfo = moduleInfo;
		this.modelContext = modelContext;
	}

	@Override
	public String getModuleName() {
		return moduleInfo.name().toString();
	}

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		if ( usageMap == null ) {
			usageMap = AnnotationUsageBuilder.collectUsages( moduleInfo.annotations(), modelContext );
		}
		return usageMap;
	}
}
