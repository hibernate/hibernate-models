/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a double
 *
 * @author Steve Ebersole
 */
public class DoubleValueConverter implements JandexValueConverter<Double> {
	public static final DoubleValueConverter JANDEX_DOUBLE_VALUE_WRAPPER = new DoubleValueConverter();

	@Override
	public Double convert(AnnotationValue jandexValue, ModelsContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asDouble();
	}
}
