/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationTargetSupport;
import org.hibernate.models.spi.ModelsContext;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTargetSupport {
	private final ModelsContext modelContext;

	private Map<Class<? extends Annotation>, ? extends Annotation> usageMap;

	public AbstractAnnotationTarget(ModelsContext modelContext) {
		this.modelContext = modelContext;
	}

	public ModelsContext getModelContext() {
		return modelContext;
	}

	/**
	 * The Jandex AnnotationTarget we can use to read the AnnotationInstance from
	 * which to build the {@linkplain #getUsageMap() AnnotationUsage map}
	 */
	protected abstract org.jboss.jandex.AnnotationTarget getJandexAnnotationTarget();

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		if ( usageMap == null ) {
			usageMap = AnnotationUsageBuilder.collectUsages( getJandexAnnotationTarget(), modelContext );
		}
		return usageMap;
	}

	@Override
	public void clearAnnotationUsages() {
		getUsageMap().clear();
	}
}
