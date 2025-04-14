/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a character
 *
 * @author Steve Ebersole
 */
public class CharacterValueConverter implements JandexValueConverter<Character> {
	public static final CharacterValueConverter JANDEX_CHARACTER_VALUE_WRAPPER = new CharacterValueConverter();

	@Override
	public Character convert(AnnotationValue jandexValue, ModelsContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asChar();
	}
}
