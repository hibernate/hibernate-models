/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting string values
 *
 * @author Steve Ebersole
 */
public class StringValueExtractor extends AbstractValueExtractor<String> {
	public static final StringValueExtractor STRING_EXTRACTOR = new StringValueExtractor();

	@Override
	protected String extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		return StringValueConverter.STRING_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}
}
