/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting double values
 *
 * @author Steve Ebersole
 */
public class DoubleValueExtractor extends AbstractValueExtractor<Double> {
	public static final DoubleValueExtractor DOUBLE_EXTRACTOR = new DoubleValueExtractor();

	@Override
	protected Double extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return DoubleValueConverter.DOUBLE_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}
}
