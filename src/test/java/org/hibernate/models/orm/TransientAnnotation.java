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

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class TransientAnnotation implements Transient {
	public TransientAnnotation(SourceModelBuildingContext modelContext) {
	}
	public TransientAnnotation(Transient source, SourceModelBuildingContext modelContext) {
	}
	public TransientAnnotation(AnnotationInstance source, SourceModelBuildingContext modelContext) {
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return Transient.class;
	}
}
