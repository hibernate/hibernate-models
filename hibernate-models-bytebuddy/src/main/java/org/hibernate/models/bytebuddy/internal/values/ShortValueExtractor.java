/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting short values
 *
 * @author Steve Ebersole
 */
public class ShortValueExtractor extends AbstractValueExtractor<Short> {
	public static final ShortValueExtractor SHORT_EXTRACTOR = new ShortValueExtractor();

	protected Short extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return ShortValueConverter.SHORT_VALUE_WRAPPER.convert( byteBuddyValue, modelsContext );
	}

}
