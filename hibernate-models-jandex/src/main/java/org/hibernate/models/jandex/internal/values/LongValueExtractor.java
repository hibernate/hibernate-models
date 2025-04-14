/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class LongValueExtractor extends AbstractValueExtractor<Long> {
	public static final LongValueExtractor JANDEX_LONG_EXTRACTOR = new LongValueExtractor();

	@Override
	protected Long extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;
		return LongValueConverter.JANDEX_LONG_VALUE_WRAPPER.convert( jandexValue, modelsContext );
	}
}
