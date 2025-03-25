/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.internal.AnnotationTargetSupport;

import net.bytebuddy.description.annotation.AnnotationSource;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTargetSupport {
	private final ByteBuddyModelsContext modelContext;

	private Map<Class<? extends Annotation>, ? extends Annotation> usageMap;

	public AbstractAnnotationTarget(ByteBuddyModelsContext modelContext) {
		this.modelContext = modelContext;
	}

	public ByteBuddyModelsContext getModelContext() {
		return modelContext;
	}

	protected abstract AnnotationSource getAnnotationSource();

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		if ( usageMap == null ) {
			usageMap = ByteBuddyBuilders.collectUsages( getAnnotationSource(), modelContext );
		}
		return usageMap;
	}

	@Override
	public void clearAnnotationUsages() {
		getUsageMap().clear();
	}

}
