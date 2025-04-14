/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting int values
 *
 * @author Steve Ebersole
 */
public class IntegerValueExtractor extends AbstractValueExtractor<Integer> {
	public static final IntegerValueExtractor INTEGER_EXTRACTOR = new IntegerValueExtractor();

	@Override
	protected Integer extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return IntegerValueConverter.INTEGER_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}
}
