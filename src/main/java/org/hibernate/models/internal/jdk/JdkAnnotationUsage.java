/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.MutableAnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class JdkAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final A annotation;
	private final AnnotationDescriptor<A> annotationDescriptor;
	private final AnnotationTarget location;

	private final Map<String,?> valueMap;

	public JdkAnnotationUsage(
			A annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget location,
			SourceModelBuildingContext buildingContext) {
		this.annotation = annotation;
		this.annotationDescriptor = annotationDescriptor;
		this.location = location;

		this.valueMap = AnnotationUsageBuilder.extractAttributeValues( annotation, annotationDescriptor, location, buildingContext );
	}

	@Override
	public AnnotationDescriptor<A> getAnnotationDescriptor() {
		return annotationDescriptor;
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return location;
	}

	@Override
	public A toAnnotation() {
		return annotation;
	}

	@Override
	public <W> W findAttributeValue(String name) {
		//noinspection unchecked
		return (W) valueMap.get( name );
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		// for set, we need to check up front
		//		NOTE : the call to #getAttribute throws the exception if it does not
		// todo : do we want to add a distinction for a checked versus unchecked set?
		//		- i.e. setAttributeValueSafely
		annotationDescriptor.getAttribute( name );

		//noinspection unchecked
		return (V) ( (Map<String,Object>) valueMap ).put( name, value );
	}

	@Override
	public String toString() {
		return "JdkAnnotationUsage(" + annotationDescriptor.getAnnotationType().getName() + ")";
	}
}
