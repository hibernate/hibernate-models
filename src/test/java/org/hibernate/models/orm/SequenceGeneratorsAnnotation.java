/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SequenceGenerators;

import static org.hibernate.models.orm.JpaAnnotations.SEQUENCE_GENERATORS;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class SequenceGeneratorsAnnotation implements SequenceGenerators, RepeatableContainer<SequenceGenerator> {
	private SequenceGenerator[] value;

	public SequenceGeneratorsAnnotation() {
		value = new SequenceGenerator[0];
	}

	public SequenceGeneratorsAnnotation(SequenceGenerators usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, SEQUENCE_GENERATORS, modelContext );
	}


	public SequenceGeneratorsAnnotation(AnnotationInstance usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, SEQUENCE_GENERATORS, modelContext );
	}

	@Override
	public SequenceGenerator[] value() {
		return value;
	}

	@Override
	public void value(SequenceGenerator[] value) {
		this.value = value;
	}

	@Override
	public Class<? extends Annotation> annotationType() {
		return SequenceGenerators.class;
	}
}
