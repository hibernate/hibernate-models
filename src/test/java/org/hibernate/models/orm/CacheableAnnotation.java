/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

import jakarta.persistence.Cacheable;

import static org.hibernate.models.internal.jandex.JandexValueHelper.extractOptionalValue;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class CacheableAnnotation implements Cacheable {
	private boolean value;

	public CacheableAnnotation() {
		value = true;
	}

	public CacheableAnnotation(Cacheable usage, SourceModelBuildingContext modelContext) {
		value = usage.value();
	}

	public CacheableAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		value = extractOptionalValue( usage, JpaAnnotations.CACHEABLE, "value", modelContext );
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
