/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.StringValueExtractor;
import org.hibernate.models.internal.jandex.StringValueWrapper;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jdk.PassThruWrapper.PASS_THRU_WRAPPER;

/**
 * Descriptor for string values
 *
 * @author Steve Ebersole
 */
public class StringTypeDescriptor extends AbstractTypeDescriptor<String> {
	public static final StringTypeDescriptor STRING_TYPE_DESCRIPTOR = new StringTypeDescriptor();

	@Override
	public Class<String> getWrappedValueType() {
		return String.class;
	}

	@Override
	public ValueWrapper<String, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return StringValueWrapper.JANDEX_STRING_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, String> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return StringValueExtractor.JANDEX_STRING_EXTRACTOR;
	}

	@Override
	public ValueWrapper<String, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PASS_THRU_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, String> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PassThruExtractor.PASS_THRU_EXTRACTOR;
	}

	@Override
	public Object unwrap(String value) {
		return value;
	}
}
