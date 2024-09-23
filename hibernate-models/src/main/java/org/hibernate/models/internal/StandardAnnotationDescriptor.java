/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Simple implementation of {@link AnnotationDescriptor}.
 *
 * @author Steve Ebersole
 */
public class StandardAnnotationDescriptor<A extends Annotation> extends AbstractAnnotationDescriptor<A> {
	private final List<AttributeDescriptor<?>> attributeDescriptors;
	private final SourceModelBuildingContext buildingContext;

	private Map<Class<? extends Annotation>, ? extends Annotation> usagesMap;

	public StandardAnnotationDescriptor(
			Class<A> annotationType,
			SourceModelBuildingContext buildingContext) {
		this( annotationType, null, buildingContext );
	}

	public StandardAnnotationDescriptor(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext buildingContext) {
		super( annotationType, AnnotationHelper.extractTargets( annotationType ), AnnotationHelper.isInherited( annotationType ), repeatableContainer );

		this.buildingContext = buildingContext;
		this.attributeDescriptors = AnnotationDescriptorBuilding.extractAttributeDescriptors( annotationType );
	}

	@Override
	public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
		if ( usagesMap == null ) {
			usagesMap = buildUsagesMap( getAnnotationType(), buildingContext );
		}
		return usagesMap;
	}

	@Override
	public A createUsage(A jdkAnnotation, SourceModelBuildingContext context) {
		return jdkAnnotation;
	}

	@Override
	public A createUsage(SourceModelBuildingContext context) {
		throw new UnsupportedOperationException(
				"Creating empty annotation usage mot supported from StandardAnnotationDescriptor : " + getAnnotationType().getName()
		);
	}

	@Override
	public List<AttributeDescriptor<?>> getAttributes() {
		return attributeDescriptors;
	}

	/**
	 * Builds a map of the usages for the annotation's annotations
	 */
	private static <A extends Annotation> Map<Class<? extends Annotation>, ? extends Annotation> buildUsagesMap(
			Class<A> annotationType,
			SourceModelBuildingContext buildingContext) {
		final Map<Class<? extends Annotation>, ? extends Annotation> result = new HashMap<>();

		final AnnotationDescriptorRegistry annotationDescriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		final Annotation[] annotationTypeAnnotations = annotationType.getAnnotations();
		for ( int i = 0; i < annotationTypeAnnotations.length; i++ ) {
			final Annotation annotationTypeAnnotation = annotationTypeAnnotations[i];
			final Class<? extends Annotation> annotationTypeAnnotationType = annotationTypeAnnotation.annotationType();

			// skip a few well-know ones that are irrelevant
			if ( annotationTypeAnnotationType == Repeatable.class
					|| annotationTypeAnnotationType == Target.class
					|| annotationTypeAnnotationType == Retention.class
					|| annotationTypeAnnotationType == Documented.class ) {
				continue;
			}

			//noinspection rawtypes
			final AnnotationDescriptor annotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationTypeAnnotationType );
			//noinspection unchecked
			final Annotation annotationTypeAnnotationUsage = annotationDescriptor.createUsage( annotationTypeAnnotation, buildingContext );

			//noinspection rawtypes,unchecked
			( (Map) result ).put( annotationTypeAnnotationType, annotationTypeAnnotationUsage );
		}

		return result;
	}
}
