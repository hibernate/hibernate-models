/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a float
 *
 * @author Steve Ebersole
 */
public class FloatValueConverter implements JandexValueConverter<Float> {
	public static final FloatValueConverter JANDEX_FLOAT_VALUE_WRAPPER = new FloatValueConverter();

	@Override
	public Float convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asFloat();
	}
}
