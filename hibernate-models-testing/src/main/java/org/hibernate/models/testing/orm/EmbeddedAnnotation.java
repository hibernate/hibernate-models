/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.Embedded;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class EmbeddedAnnotation implements Embedded {
	public EmbeddedAnnotation(ModelsContext modelContext) {
	}

	public EmbeddedAnnotation(Embedded usage, ModelsContext modelContext) {
	}

	public EmbeddedAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Embedded.class;
	}
}
