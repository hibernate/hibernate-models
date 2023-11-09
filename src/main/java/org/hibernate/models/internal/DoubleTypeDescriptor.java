/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.DoubleValueExtractor;
import org.hibernate.models.internal.jandex.DoubleValueWrapper;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jdk.PassThruWrapper.PASS_THRU_WRAPPER;

/**
 * Descriptor for double values
 *
 * @author Steve Ebersole
 */
public class DoubleTypeDescriptor extends AbstractTypeDescriptor<Double> {
	public static final DoubleTypeDescriptor DOUBLE_TYPE_DESCRIPTOR = new DoubleTypeDescriptor();

	@Override
	public Class<Double> getWrappedValueType() {
		return Double.class;
	}

	@Override
	public ValueWrapper<Double, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return DoubleValueWrapper.JANDEX_DOUBLE_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, Double> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return DoubleValueExtractor.JANDEX_DOUBLE_EXTRACTOR;
	}

	@Override
	public ValueWrapper<Double, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PASS_THRU_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, Double> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PassThruExtractor.PASS_THRU_EXTRACTOR;
	}
}
