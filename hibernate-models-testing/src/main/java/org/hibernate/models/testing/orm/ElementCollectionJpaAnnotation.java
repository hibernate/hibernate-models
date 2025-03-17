/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.ElementCollection;

public class ElementCollectionJpaAnnotation implements ElementCollection, AttributeMarker.Fetchable {
	private Class<?> targetClass;
	private jakarta.persistence.FetchType fetch;

	/**
	 * Used in creating dynamic annotation instances (e.g. from XML)
	 */
	public ElementCollectionJpaAnnotation(SourceModelBuildingContext modelContext) {
		this.targetClass = void.class;
		this.fetch = jakarta.persistence.FetchType.LAZY;
	}

	/**
	 * Used in creating annotation instances from JDK variant
	 */
	public ElementCollectionJpaAnnotation(ElementCollection annotation, SourceModelBuildingContext modelContext) {
		this.targetClass = annotation.targetClass();
		this.fetch = annotation.fetch();
	}

	/**
	 * Used in creating annotation instances from Jandex variant
	 */
	public ElementCollectionJpaAnnotation(
			Map<String, Object> attributeValues,
			SourceModelBuildingContext modelContext) {
		this.targetClass = (Class<?>) attributeValues.get( "targetClass" );
		this.fetch = (jakarta.persistence.FetchType) attributeValues.get( "fetch" );
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return ElementCollection.class;
	}

	@Override
	public Class<?> targetClass() {
		return targetClass;
	}

	public void targetClass(Class<?> value) {
		this.targetClass = value;
	}


	@Override
	public jakarta.persistence.FetchType fetch() {
		return fetch;
	}

	public void fetch(jakarta.persistence.FetchType value) {
		this.fetch = value;
	}


}
