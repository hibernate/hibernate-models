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
public class StringValueExtractor extends AbstractValueExtractor<String> {
	public static final StringValueExtractor JANDEX_STRING_EXTRACTOR = new StringValueExtractor();

	@Override
	protected String extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;
		return StringValueConverter.JANDEX_STRING_VALUE_WRAPPER.convert( jandexValue, modelsContext );
	}
}
