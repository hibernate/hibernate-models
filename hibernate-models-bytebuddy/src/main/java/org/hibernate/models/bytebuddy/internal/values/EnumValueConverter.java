/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.description.enumeration.EnumerationDescription;

/**
 * Support for converting enum values
 *
 * @author Steve Ebersole
 */
public class EnumValueConverter<E extends Enum<E>> implements ValueConverter<E> {
	private final Class<E> enumClass;

	public EnumValueConverter(Class<E> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public E convert(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelContext) {
		assert byteBuddyValue != null;
		final EnumerationDescription resolved = byteBuddyValue.resolve( EnumerationDescription.class );
		return resolved.load( enumClass );
	}
}
