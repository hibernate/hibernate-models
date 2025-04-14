/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting short values
 *
 * @author Steve Ebersole
 */
public class ShortValueConverter implements ValueConverter<Short> {
	public static final ShortValueConverter SHORT_VALUE_WRAPPER = new ShortValueConverter();

	@Override
	public Short convert(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Short.class );
	}
}
