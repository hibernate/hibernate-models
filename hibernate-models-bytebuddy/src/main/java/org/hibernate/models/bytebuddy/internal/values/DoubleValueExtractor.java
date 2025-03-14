/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting double values
 *
 * @author Steve Ebersole
 */
public class DoubleValueExtractor extends AbstractValueExtractor<Double> {
	public static final DoubleValueExtractor DOUBLE_EXTRACTOR = new DoubleValueExtractor();

	@Override
	protected Double extractAndWrap(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext buildingContext) {
		assert byteBuddyValue != null;
		return DoubleValueConverter.DOUBLE_VALUE_WRAPPER.convert( byteBuddyValue, buildingContext );
	}
}
