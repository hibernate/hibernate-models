/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting byte values
 *
 * @author Steve Ebersole
 */
public class ByteValueExtractor extends AbstractValueExtractor<Byte> {
	public static final ByteValueExtractor BYTE_EXTRACTOR = new ByteValueExtractor();

	@Override
	protected Byte extractAndWrap(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext buildingContext) {
		assert byteBuddyValue != null;
		return ByteValueConverter.BYTE_VALUE_WRAPPER.convert( byteBuddyValue, buildingContext );
	}
}
