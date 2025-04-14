/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting int values
 *
 * @author Steve Ebersole
 */
public class IntegerValueConverter implements ValueConverter<Integer> {
	public static final IntegerValueConverter INTEGER_VALUE_WRAPPER = new IntegerValueConverter();

	@Override
	public Integer convert(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Integer.class );
	}
}
