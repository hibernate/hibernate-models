/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.Cacheable;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class CacheableAnnotation implements Cacheable {
	private boolean value;

	public CacheableAnnotation(SourceModelBuildingContext modelContext) {
		value = true;
	}

	public CacheableAnnotation(Cacheable usage, SourceModelBuildingContext modelContext) {
		value = usage.value();
	}

	public CacheableAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		value = (boolean) attributeValues.get( "value" );
	}

	@Override
	public boolean value() {
		return value;
	}

	public void value(boolean value) {
		this.value = value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Cacheable.class;
	}
}
