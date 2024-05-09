/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jandex.BooleanValueConverter;
import org.hibernate.models.internal.jandex.BooleanValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;

/**
 * Descriptor for boolean values
 *
 * @author Steve Ebersole
 */
public class BooleanTypeDescriptor extends AbstractTypeDescriptor<Boolean> {
	public static final BooleanTypeDescriptor BOOLEAN_TYPE_DESCRIPTOR = new BooleanTypeDescriptor();

	@Override
	public Class<Boolean> getValueType() {
		return Boolean.class;
	}

	@Override
	public JandexValueConverter<Boolean> createJandexValueConverter(SourceModelBuildingContext buildingContext) {
		return BooleanValueConverter.JANDEX_BOOLEAN_VALUE_WRAPPER;
	}

	@Override
	public JandexValueExtractor<Boolean> createJandexValueExtractor(SourceModelBuildingContext buildingContext) {
		return BooleanValueExtractor.JANDEX_BOOLEAN_EXTRACTOR;
	}

	@Override
	public Object unwrap(Boolean value) {
		return value;
	}

	@Override
	public Boolean[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Boolean[size];
	}
}
