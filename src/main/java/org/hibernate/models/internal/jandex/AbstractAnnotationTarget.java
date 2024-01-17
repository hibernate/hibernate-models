/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.internal.AnnotationTargetSupport;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTargetSupport {
	private final SourceModelBuildingContext buildingContext;

	private Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap;

	public AbstractAnnotationTarget(SourceModelBuildingContext buildingContext) {
		this.buildingContext = buildingContext;
	}

	/**
	 * The Jandex AnnotationTarget we can use to read the AnnotationInstance from
	 * which to build the {@linkplain #getUsageMap() AnnotationUsage map}
	 */
	protected abstract org.jboss.jandex.AnnotationTarget getJandexAnnotationTarget();

	@Override
	public Map<Class<? extends Annotation>, AnnotationUsage<?>> getUsageMap() {
		if ( usageMap == null ) {
			usageMap = AnnotationUsageBuilder.collectUsages( getJandexAnnotationTarget(), this, buildingContext );
		}
		return usageMap;
	}

	@Override
	public void clearAnnotationUsages() {
		getUsageMap().clear();
	}

	@Override
	public <X extends Annotation> void removeAnnotationUsage(Class<X> annotationType) {
		usageMap.remove( annotationType );
	}

	@Override
	public <X extends Annotation> void addAnnotationUsage(AnnotationUsage<X> annotationUsage) {
		getUsageMap().put( annotationUsage.getAnnotationType(), annotationUsage );
	}

	@Override
	public SourceModelBuildingContext getBuildingContext() {
		return buildingContext;
	}
}
