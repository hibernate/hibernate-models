/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jandex.ByteValueConverter;
import org.hibernate.models.internal.jandex.ByteValueExtractor;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for byte values
 *
 * @author Steve Ebersole
 */
public class ByteTypeDescriptor extends AbstractTypeDescriptor<Byte> {
	public static final ByteTypeDescriptor BYTE_TYPE_DESCRIPTOR = new ByteTypeDescriptor();

	@Override
	public Class<Byte> getValueType() {
		return Byte.class;
	}

	@Override
	public JandexValueConverter<Byte> createJandexValueConverter(SourceModelBuildingContext buildingContext) {
		return ByteValueConverter.JANDEX_BYTE_VALUE_WRAPPER;
	}

	@Override
	public JandexValueExtractor<Byte> createJandexValueExtractor(SourceModelBuildingContext buildingContext) {
		return ByteValueExtractor.JANDEX_BYTE_EXTRACTOR;
	}

	@Override
	public Object unwrap(Byte value) {
		return value;
	}

	@Override
	public Byte[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Byte[size];
	}
}
