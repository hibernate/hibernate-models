/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.FloatValueExtractor;
import org.hibernate.models.internal.jandex.FloatValueWrapper;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jdk.PassThruWrapper.PASS_THRU_WRAPPER;

/**
 * Descriptor for float values
 *
 * @author Steve Ebersole
 */
public class FloatTypeDescriptor extends AbstractTypeDescriptor<Float> {
	public static final FloatTypeDescriptor FLOAT_TYPE_DESCRIPTOR = new FloatTypeDescriptor();

	@Override
	public Class<Float> getWrappedValueType() {
		return Float.class;
	}

	@Override
	public ValueWrapper<Float, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return FloatValueWrapper.JANDEX_FLOAT_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, Float> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return FloatValueExtractor.JANDEX_FLOAT_EXTRACTOR;
	}

	@Override
	public ValueWrapper<Float, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PASS_THRU_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, Float> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PassThruExtractor.PASS_THRU_EXTRACTOR;
	}

	@Override
	public Object unwrap(Float value) {
		return value;
	}

	@Override
	public void render(RenderingCollector collector, String name, Object attributeValue) {
		collector.addLine( "%s = %sF", name, attributeValue );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue) {
		collector.addLine( "%sF", attributeValue );
	}
}
