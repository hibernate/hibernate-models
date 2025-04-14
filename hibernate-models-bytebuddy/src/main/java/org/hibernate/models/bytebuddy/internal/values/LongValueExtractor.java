/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting long values
 *
 * @author Steve Ebersole
 */
public class LongValueExtractor extends AbstractValueExtractor<Long> {
	public static final LongValueExtractor LONG_EXTRACTOR = new LongValueExtractor();

	@Override
	protected Long extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return LongValueConverter.LONG_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}
}
