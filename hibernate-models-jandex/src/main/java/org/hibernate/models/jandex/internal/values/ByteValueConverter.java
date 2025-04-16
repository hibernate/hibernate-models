/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a byte
 *
 * @author Steve Ebersole
 */
public class ByteValueConverter implements JandexValueConverter<Byte> {
	public static final ByteValueConverter JANDEX_BYTE_VALUE_WRAPPER = new ByteValueConverter();

	@Override
	public Byte convert(AnnotationValue jandexValue, ModelsContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asByte();
	}
}
