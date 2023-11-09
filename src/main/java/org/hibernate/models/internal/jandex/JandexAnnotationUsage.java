/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.MutableAnnotationUsage;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * AnnotationUsage implementation based on the Jandex AnnotationInstance
 *
 * @author Steve Ebersole
 */
public class JandexAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final Class<A> annotationType;
	private final AnnotationTarget annotationTarget;

	private final Map<String,?> attributeValueMap;

	public JandexAnnotationUsage(
			AnnotationInstance annotationInstance,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget annotationTarget,
			SourceModelBuildingContext processingContext) {
		assert annotationInstance != null : "Jandex AnnotationInstance was null";
		assert annotationDescriptor != null : "AnnotationDescriptor was null - " + annotationInstance;

		this.annotationTarget = annotationTarget;
		this.annotationType = annotationDescriptor.getAnnotationType();

		this.attributeValueMap = AnnotationUsageBuilder.extractAttributeValues(
				annotationInstance,
				annotationDescriptor,
				annotationTarget,
				processingContext
		);
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return annotationTarget;
	}

	@Override
	public <W> W getAttributeValue(String name) {
		//noinspection unchecked
		return (W) attributeValueMap.get( name );
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		//noinspection unchecked
		return (V) ( (Map<String,Object>) attributeValueMap ).put( name, value );
	}
}
