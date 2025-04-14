/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a float
 *
 * @author Steve Ebersole
 */
public class StringValueConverter implements JandexValueConverter<String> {
	public static final StringValueConverter JANDEX_STRING_VALUE_WRAPPER = new StringValueConverter();

	@Override
	public String convert(AnnotationValue jandexValue, ModelsContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asString();
	}
}
