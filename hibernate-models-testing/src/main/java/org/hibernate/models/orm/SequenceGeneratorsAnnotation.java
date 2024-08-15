/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SequenceGenerators;

import static org.hibernate.models.orm.JpaAnnotations.SEQUENCE_GENERATORS;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class SequenceGeneratorsAnnotation implements SequenceGenerators, RepeatableContainer<SequenceGenerator> {
	private SequenceGenerator[] value;

	public SequenceGeneratorsAnnotation(SourceModelBuildingContext modelContext) {
		value = new SequenceGenerator[0];
	}

	public SequenceGeneratorsAnnotation(SequenceGenerators usage, SourceModelBuildingContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, SEQUENCE_GENERATORS, modelContext );
	}


	public SequenceGeneratorsAnnotation(Map<String,Object> attributeValues, SourceModelBuildingContext modelContext) {
		value = (SequenceGenerator[]) attributeValues.get( "value" );
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
