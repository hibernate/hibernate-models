/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting byte values
 *
 * @author Steve Ebersole
 */
public class ByteValueConverter implements ValueConverter<Byte> {
	public static final ByteValueConverter BYTE_VALUE_WRAPPER = new ByteValueConverter();

	@Override
	public Byte convert(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext modelContext) {
		assert byteBuddyValue != null;
		return byteBuddyValue.resolve( Byte.class );
	}
}
