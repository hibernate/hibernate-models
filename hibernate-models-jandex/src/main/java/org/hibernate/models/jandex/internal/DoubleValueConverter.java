/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a double
 *
 * @author Steve Ebersole
 */
public class DoubleValueConverter implements JandexValueConverter<Double> {
	public static final DoubleValueConverter JANDEX_DOUBLE_VALUE_WRAPPER = new DoubleValueConverter();

	@Override
	public Double convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asDouble();
	}
}
