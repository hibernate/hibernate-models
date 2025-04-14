/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting char values
 *
 * @author Steve Ebersole
 */
public class CharacterValueExtractor extends AbstractValueExtractor<Character> {
	public static final CharacterValueExtractor CHARACTER_EXTRACTOR = new CharacterValueExtractor();

	@Override
	protected Character extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return CharacterValueConverter.CHARACTER_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}
}
