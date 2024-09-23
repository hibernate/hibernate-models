/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.internal.AnnotationTargetSupport;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTargetSupport {
	private final SourceModelBuildingContext modelContext;
	private final Map<Class<? extends Annotation>, ? extends Annotation> usageMap = new HashMap<>();

	public AbstractAnnotationTarget(SourceModelBuildingContext modelContext) {
		this.modelContext = modelContext;
	}

	public SourceModelBuildingContext getModelContext() {
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
