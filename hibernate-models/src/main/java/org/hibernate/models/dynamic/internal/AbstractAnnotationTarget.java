/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.dynamic.internal;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.support.AnnotationTargetSupport;
import org.hibernate.models.spi.ModelsContext;

/// Base support for AnnotationTarget as a dynamic model
///
/// @author Steve Ebersole
public abstract class AbstractAnnotationTarget implements AnnotationTargetSupport {
	private final ModelsContext modelContext;
	private final Map<Class<? extends Annotation>, ? extends Annotation> usageMap = new HashMap<>();

	public AbstractAnnotationTarget(ModelsContext modelContext) {
		this.modelContext = modelContext;
	}

	public ModelsContext getModelContext() {
		return modelContext;
	}

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		return usageMap;
	}

	@Override
	public void clearAnnotationUsages() {
		usageMap.clear();
	}
}
