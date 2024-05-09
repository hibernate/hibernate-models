/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueConverter;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a character
 *
 * @author Steve Ebersole
 */
public class CharacterValueConverter implements JandexValueConverter<Character> {
	public static final CharacterValueConverter JANDEX_CHARACTER_VALUE_WRAPPER = new CharacterValueConverter();

	@Override
	public Character convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asChar();
	}
}
