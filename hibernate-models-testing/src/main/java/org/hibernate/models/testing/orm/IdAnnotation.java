/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.Id;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class IdAnnotation implements Id {
	public IdAnnotation(ModelsContext modelContext) {
	}

	public IdAnnotation(Id jdkAnnotation, ModelsContext modelContext) {
	}

	public IdAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Id.class;
	}
}
