/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting char values
 *
 * @author Steve Ebersole
 */
public class CharacterValueConverter implements ValueConverter<Character> {
	public static final CharacterValueConverter CHARACTER_VALUE_WRAPPER = new CharacterValueConverter();

	@Override
	public Character convert(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Character.class );
	}
}
