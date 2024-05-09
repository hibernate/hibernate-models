/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jandex.DoubleValueConverter;
import org.hibernate.models.internal.jandex.DoubleValueExtractor;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for double values
 *
 * @author Steve Ebersole
 */
public class DoubleTypeDescriptor extends AbstractTypeDescriptor<Double> {
	public static final DoubleTypeDescriptor DOUBLE_TYPE_DESCRIPTOR = new DoubleTypeDescriptor();

	@Override
	public Class<Double> getValueType() {
		return Double.class;
	}

	@Override
	public JandexValueConverter<Double> createJandexValueConverter(SourceModelBuildingContext modelContext) {
		return DoubleValueConverter.JANDEX_DOUBLE_VALUE_WRAPPER;
	}

	@Override
	public JandexValueExtractor<Double> createJandexValueExtractor(SourceModelBuildingContext modelContext) {
		return DoubleValueExtractor.JANDEX_DOUBLE_EXTRACTOR;
	}

	@Override
	public Object unwrap(Double value) {
		return value;
	}

	@Override
	public Double[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Double[size];
	}
}
