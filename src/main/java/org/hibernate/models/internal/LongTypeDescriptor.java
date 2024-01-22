/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.LongValueExtractor;
import org.hibernate.models.internal.jandex.LongValueWrapper;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jdk.PassThruWrapper.PASS_THRU_WRAPPER;

/**
 * Descriptor for long values
 *
 * @author Steve Ebersole
 */
public class LongTypeDescriptor extends AbstractTypeDescriptor<Long> {
	public static final LongTypeDescriptor LONG_TYPE_DESCRIPTOR = new LongTypeDescriptor();

	@Override
	public Class<Long> getWrappedValueType() {
		return Long.class;
	}

	@Override
	public ValueWrapper<Long, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return LongValueWrapper.JANDEX_LONG_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, Long> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return LongValueExtractor.JANDEX_LONG_EXTRACTOR;
	}

	@Override
	public ValueWrapper<Long, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PASS_THRU_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, Long> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PassThruExtractor.PASS_THRU_EXTRACTOR;
	}

	@Override
	public Object unwrap(Long value) {
		return value;
	}
}
