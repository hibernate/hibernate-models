/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.Entity;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class EntityAnnotation implements Entity {
	private String name;

	public EntityAnnotation(ModelsContext modelContext) {
		name = "";
	}

	public EntityAnnotation(Entity jdkAnnotation, ModelsContext modelContext) {
		this.name = jdkAnnotation.name();
	}

	public EntityAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
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
