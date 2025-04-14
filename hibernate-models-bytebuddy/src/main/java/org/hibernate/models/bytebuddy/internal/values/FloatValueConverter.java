/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting float values
 *
 * @author Steve Ebersole
 */
public class FloatValueConverter implements ValueConverter<Float> {
	public static final FloatValueConverter FLOAT_VALUE_WRAPPER = new FloatValueConverter();

	@Override
	public Float convert(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Float.class );
	}
}
