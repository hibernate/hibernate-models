/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;
import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting double values
 *
 * @author Steve Ebersole
 */
public class DoubleValueConverter implements ValueConverter<Double> {
	public static final DoubleValueConverter DOUBLE_VALUE_WRAPPER = new DoubleValueConverter();

	@Override
	public Double convert(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Double.class );
	}
}
