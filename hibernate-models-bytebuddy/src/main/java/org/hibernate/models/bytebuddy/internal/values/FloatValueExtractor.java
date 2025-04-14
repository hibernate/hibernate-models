/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting float values
 *
 * @author Steve Ebersole
 */
public class FloatValueExtractor extends AbstractValueExtractor<Float> {
	public static final FloatValueExtractor FLOAT_EXTRACTOR = new FloatValueExtractor();

	@Override
	protected Float extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return FloatValueConverter.FLOAT_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}
}
