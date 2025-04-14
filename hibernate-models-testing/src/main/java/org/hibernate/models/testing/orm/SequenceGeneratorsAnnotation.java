/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.internal.AnnotationUsageHelper;
import org.hibernate.models.spi.ModelsContext;

import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SequenceGenerators;

import static org.hibernate.models.testing.orm.JpaAnnotations.SEQUENCE_GENERATORS;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings({ "ClassExplicitlyAnnotation", "unused" })
public class SequenceGeneratorsAnnotation implements SequenceGenerators, RepeatableContainer<SequenceGenerator> {
	private SequenceGenerator[] value;

	public SequenceGeneratorsAnnotation(ModelsContext modelContext) {
		value = new SequenceGenerator[0];
	}

	public SequenceGeneratorsAnnotation(SequenceGenerators usage, ModelsContext modelContext) {
		value = AnnotationUsageHelper.extractRepeatedValues( usage, SEQUENCE_GENERATORS, modelContext );
	}


	public SequenceGeneratorsAnnotation(Map<String,Object> attributeValues, ModelsContext modelContext) {
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
