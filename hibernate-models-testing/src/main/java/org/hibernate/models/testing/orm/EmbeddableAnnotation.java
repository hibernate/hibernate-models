/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.Embeddable;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class EmbeddableAnnotation implements Embeddable {
	public EmbeddableAnnotation(ModelsContext modelContext) {
	}

	public EmbeddableAnnotation(Embeddable usage, ModelsContext modelContext) {
	}

	public EmbeddableAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Embeddable.class;
	}
}
