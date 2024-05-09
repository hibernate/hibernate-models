/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jandex.LongValueConverter;
import org.hibernate.models.internal.jandex.LongValueExtractor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;

/**
 * Descriptor for long values
 *
 * @author Steve Ebersole
 */
public class LongTypeDescriptor extends AbstractTypeDescriptor<Long> {
	public static final LongTypeDescriptor LONG_TYPE_DESCRIPTOR = new LongTypeDescriptor();

	@Override
	public Class<Long> getValueType() {
		return Long.class;
	}

	@Override
	public JandexValueConverter<Long> createJandexValueConverter(SourceModelBuildingContext modelContext) {
		return LongValueConverter.JANDEX_LONG_VALUE_WRAPPER;
	}

	@Override
	public JandexValueExtractor<Long> createJandexValueExtractor(SourceModelBuildingContext modelContext) {
		return LongValueExtractor.JANDEX_LONG_EXTRACTOR;
	}

	@Override
	public Object unwrap(Long value) {
		return value;
	}

	@Override
	public void render(RenderingCollector collector, String name, Object attributeValue, SourceModelBuildingContext modelContext) {
		collector.addLine( "%s = %sL", name, attributeValue );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext) {
		collector.addLine( "%sL", attributeValue );
	}

	@Override
	public Long[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Long[size];
	}
}
