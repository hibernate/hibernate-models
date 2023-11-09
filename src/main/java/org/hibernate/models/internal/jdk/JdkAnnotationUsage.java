/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.MutableAnnotationUsage;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class JdkAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final Class<A> annotationType;
	private final AnnotationTarget location;

	private final Map<String,?> valueMap;

	public JdkAnnotationUsage(
			A annotation,
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget location,
			SourceModelBuildingContext buildingContext) {
		this.annotationType = annotationDescriptor.getAnnotationType();
		this.location = location;

		this.valueMap = AnnotationUsageBuilder.extractAttributeValues( annotation, annotationDescriptor, location, buildingContext );
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return location;
	}

	@Override
	public <W> W getAttributeValue(String name) {
		//noinspection unchecked
		return (W) valueMap.get( name );
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		//noinspection unchecked
		return (V) ( (Map<String,Object>) valueMap ).put( name, value );
	}

	@Override
	public String toString() {
		return "JdkAnnotationUsage(" + annotationType + ")";
	}
}
