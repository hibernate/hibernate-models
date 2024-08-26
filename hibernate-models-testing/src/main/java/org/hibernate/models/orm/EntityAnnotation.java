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

import jakarta.persistence.Entity;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class EntityAnnotation implements Entity {
	private String name;

	public EntityAnnotation(SourceModelBuildingContext modelContext) {
		name = "";
	}

	public EntityAnnotation(Entity jdkAnnotation, SourceModelBuildingContext modelContext) {
		this.name = jdkAnnotation.name();
	}

	public EntityAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		this.name = (String) attributeValues.get( "name" );
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
