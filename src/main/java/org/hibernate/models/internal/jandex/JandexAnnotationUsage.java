/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationProxy;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.MutableAnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * AnnotationUsage implementation based on the Jandex AnnotationInstance
 *
 * @author Steve Ebersole
 */
public class JandexAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final AnnotationDescriptor<A> annotationDescriptor;

	private final Map<String,?> attributeValueMap;

	public JandexAnnotationUsage(
			AnnotationInstance annotationInstance,
			AnnotationDescriptor<A> annotationDescriptor,
			SourceModelBuildingContext processingContext) {
		assert annotationInstance != null : "Jandex AnnotationInstance was null";
		assert annotationDescriptor != null : "AnnotationDescriptor was null - " + annotationInstance;

		this.annotationDescriptor = annotationDescriptor;

		this.attributeValueMap = AnnotationUsageBuilder.extractAttributeValues(
				annotationInstance,
				annotationDescriptor,
				processingContext
		);
	}

	@Override
	public AnnotationDescriptor<A> getAnnotationDescriptor() {
		return annotationDescriptor;
	}

	@Override
	public A toAnnotation() {
		return AnnotationProxy.makeProxy( annotationDescriptor, attributeValueMap );
	}

	@Override
	public <W> W findAttributeValue(String name) {
		//noinspection unchecked
		return (W) attributeValueMap.get( name );
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		// for set, we need to check up front
		//		NOTE : the call to #getAttribute throws the exception if it does not
		annotationDescriptor.getAttribute( name );

		//noinspection unchecked
		return (V) ( (Map<String,Object>) attributeValueMap ).put( name, value );
	}

	@Override
	public String toString() {
		return "JandexAnnotationUsage(" + annotationDescriptor.getAnnotationType().getName() + ")";
	}
}
