/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting long values
 *
 * @author Steve Ebersole
 */
public class LongValueConverter implements ValueConverter<Long> {
	public static final LongValueConverter LONG_VALUE_WRAPPER = new LongValueConverter();

	@Override
	public Long convert(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Long.class );
	}
}
