/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.hibernate.models.internal.AnnotationTargetSupport;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * AnnotationTarget where we know the annotations up front, but
 * want to delay processing them until (unless!) they are needed
 *
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationTarget implements AnnotationTargetSupport {
	private final Supplier<Annotation[]> annotationSupplier;
	private final SourceModelBuildingContext modelContext;

	private Map<Class<? extends Annotation>, ? extends Annotation> usagesMap;

	public AbstractAnnotationTarget(
			Supplier<Annotation[]> annotationSupplier,
			SourceModelBuildingContext modelContext) {
		this.annotationSupplier = annotationSupplier;
		this.modelContext = modelContext;
	}

	public SourceModelBuildingContext getModelContext() {
		return modelContext;
	}

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		if ( usagesMap == null ) {
			usagesMap = buildUsagesMap();
		}
		return usagesMap;
	}

	private Map<Class<? extends Annotation>, ? extends Annotation> buildUsagesMap() {
		final Map<Class<? extends Annotation>, Annotation> result = new HashMap<>();
		for ( Annotation annotation : annotationSupplier.get() ) {
			//noinspection unchecked
			final AnnotationDescriptor<Annotation> descriptor = (AnnotationDescriptor<Annotation>) modelContext
					.getAnnotationDescriptorRegistry()
					.getDescriptor( annotation.annotationType() );
			result.put( annotation.annotationType(), descriptor.createUsage( annotation, modelContext ) );
		}
		return result;
	}

	@Override
	public void clearAnnotationUsages() {
		getUsageMap().clear();
	}

	@Override
	public <X extends Annotation> void addAnnotationUsage(X annotationUsage) {
		//noinspection unchecked,rawtypes
		( (Map) getUsageMap() ).put( annotationUsage.annotationType(), annotationUsage );
	}
}
