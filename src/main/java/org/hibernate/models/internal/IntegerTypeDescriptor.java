/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jandex.IntegerValueConverter;
import org.hibernate.models.internal.jandex.IntegerValueExtractor;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for integer values
 *
 * @author Steve Ebersole
 */
public class IntegerTypeDescriptor extends AbstractTypeDescriptor<Integer> {
	public static final IntegerTypeDescriptor INTEGER_TYPE_DESCRIPTOR = new IntegerTypeDescriptor();

	@Override
	public Class<Integer> getValueType() {
		return Integer.class;
	}

	@Override
	public JandexValueConverter<Integer> createJandexValueConverter(SourceModelBuildingContext modelContext) {
		return IntegerValueConverter.JANDEX_INTEGER_VALUE_WRAPPER;
	}

	@Override
	public JandexValueExtractor<Integer> createJandexValueExtractor(SourceModelBuildingContext modelContext) {
		return IntegerValueExtractor.JANDEX_INTEGER_EXTRACTOR;
	}

	@Override
	public Object unwrap(Integer value) {
		return value;
	}

	@Override
	public Integer[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Integer[size];
	}
}
