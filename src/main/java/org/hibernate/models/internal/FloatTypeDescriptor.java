/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.internal.jandex.FloatValueConverter;
import org.hibernate.models.internal.jandex.FloatValueExtractor;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for float values
 *
 * @author Steve Ebersole
 */
public class FloatTypeDescriptor extends AbstractTypeDescriptor<Float> {
	public static final FloatTypeDescriptor FLOAT_TYPE_DESCRIPTOR = new FloatTypeDescriptor();

	@Override
	public Class<Float> getValueType() {
		return Float.class;
	}

	@Override
	public JandexValueConverter<Float> createJandexValueConverter(SourceModelBuildingContext buildingContext) {
		return FloatValueConverter.JANDEX_FLOAT_VALUE_WRAPPER;
	}

	@Override
	public JandexValueExtractor<Float> createJandexValueExtractor(SourceModelBuildingContext buildingContext) {
		return FloatValueExtractor.JANDEX_FLOAT_EXTRACTOR;
	}

	@Override
	public Object unwrap(Float value) {
		return value;
	}

	@Override
	public void render(RenderingCollector collector, String name, Object attributeValue, SourceModelBuildingContext modelContext) {
		collector.addLine( "%s = %sF", name, attributeValue );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext) {
		collector.addLine( "%sF", attributeValue );
	}

	@Override
	public Float[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Float[size];
	}
}
