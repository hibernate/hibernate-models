/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting string values
 *
 * @author Steve Ebersole
 */
public class StringValueConverter implements ValueConverter<String> {
	public static final StringValueConverter STRING_VALUE_WRAPPER = new StringValueConverter();

	@Override
	public String convert(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( String.class );
	}
}
