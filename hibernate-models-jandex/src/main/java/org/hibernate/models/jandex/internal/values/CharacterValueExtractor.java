/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Extracts character values from an attribute
 *
 * @author Steve Ebersole
 */
public class CharacterValueExtractor extends AbstractValueExtractor<Character> {
	public static final CharacterValueExtractor JANDEX_CHARACTER_EXTRACTOR = new CharacterValueExtractor();

	@Override
	protected Character extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;
		return CharacterValueConverter.JANDEX_CHARACTER_VALUE_WRAPPER.convert( jandexValue, modelsContext );
	}
}
