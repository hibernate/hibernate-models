/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class TransientAnnotation implements Transient {
	public TransientAnnotation(ModelsContext modelContext) {
	}
	public TransientAnnotation(Transient source, ModelsContext modelContext) {
	}
	public TransientAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Transient.class;
	}
}
