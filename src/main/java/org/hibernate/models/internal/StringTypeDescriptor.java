/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jandex.StringValueConverter;
import org.hibernate.models.internal.jandex.StringValueExtractor;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for string values
 *
 * @author Steve Ebersole
 */
public class StringTypeDescriptor extends AbstractTypeDescriptor<String> {
	public static final StringTypeDescriptor STRING_TYPE_DESCRIPTOR = new StringTypeDescriptor();

	@Override
	public Class<String> getValueType() {
		return String.class;
	}

	@Override
	public JandexValueConverter<String> createJandexValueConverter(SourceModelBuildingContext buildingContext) {
		return StringValueConverter.JANDEX_STRING_VALUE_WRAPPER;
	}

	@Override
	public JandexValueExtractor<String> createJandexValueExtractor(SourceModelBuildingContext buildingContext) {
		return StringValueExtractor.JANDEX_STRING_EXTRACTOR;
	}

	@Override
	public Object unwrap(String value) {
		return value;
	}

	@Override
	public void render(
			RenderingCollector collector,
			String name,
			Object attributeValue,
			SourceModelBuildingContext modelContext) {
		collector.addLine( "%s = \"%s\"", name, attributeValue );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext) {
		collector.addLine( "\"%s\"", attributeValue );
	}

	@Override
	public String[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new String[size];
	}
}
