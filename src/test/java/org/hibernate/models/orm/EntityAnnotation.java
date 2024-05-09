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
import org.jboss.jandex.AnnotationValue;

import jakarta.persistence.Entity;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class EntityAnnotation implements Entity {
	private String name;

	public EntityAnnotation() {
		name = "";
	}

	public EntityAnnotation(Entity jdkAnnotation, SourceModelBuildingContext modelContext) {
		this.name = jdkAnnotation.name();
	}

	public EntityAnnotation(AnnotationInstance jandexAnnotation, SourceModelBuildingContext modelContext) {
		final AnnotationValue nameValue = jandexAnnotation.value( "name" );
		this.name = nameValue == null ? "" : nameValue.asString();
	}

	@Override
	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Entity.class;
	}
}
