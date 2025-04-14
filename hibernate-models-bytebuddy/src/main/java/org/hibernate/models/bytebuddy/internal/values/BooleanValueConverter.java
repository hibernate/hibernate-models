/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting boolean values
 *
 * @author Steve Ebersole
 */
public class BooleanValueConverter implements ValueConverter<Boolean> {
	public static final BooleanValueConverter BOOLEAN_VALUE_WRAPPER = new BooleanValueConverter();

	@Override
	public Boolean convert(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Boolean.class );
	}
}
