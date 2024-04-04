/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.internal.AnnotationTargetSupport;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTargetSupport {
	private final SourceModelBuildingContext buildingContext;
	private final Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap = new HashMap<>();

	public AbstractAnnotationTarget(SourceModelBuildingContext buildingContext) {
		this.buildingContext = buildingContext;
	}

	@Override
	public SourceModelBuildingContext getBuildingContext() {
		return buildingContext;
	}

	@Override
	public Map<Class<? extends Annotation>, AnnotationUsage<? extends Annotation>> getUsageMap() {
		return usageMap;
	}

	@Override
	public void clearAnnotationUsages() {
		usageMap.clear();
	}

	/**
	 * Applies the given {@code annotationUsage} to this target.
	 *
	 * @todo It is undefined currently what happens if the annotation type is already applied on this target.
	 */
	public <X extends Annotation> void addAnnotationUsage(AnnotationUsage<X> annotationUsage) {
		assert annotationUsage.getAnnotationDescriptor().getAllowableTargets().contains( getKind() );
		final AnnotationUsage<?> previous = usageMap.put( annotationUsage.getAnnotationType(), annotationUsage );

		if ( previous != null ) {
			// todo : ignore?  log?  exception?
		}
	}

}
