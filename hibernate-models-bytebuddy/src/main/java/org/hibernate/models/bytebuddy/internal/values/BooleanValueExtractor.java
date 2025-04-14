/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting boolean values
 *
 * @author Steve Ebersole
 */
public class BooleanValueExtractor extends AbstractValueExtractor<Boolean> {
	public static final BooleanValueExtractor BOOLEAN_EXTRACTOR = new BooleanValueExtractor();

	@Override
	protected Boolean extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return BooleanValueConverter.BOOLEAN_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}
}
