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
public class ShortValueExtractor extends AbstractValueExtractor<Short> {
	public static final ShortValueExtractor JANDEX_SHORT_EXTRACTOR = new ShortValueExtractor();

	protected Short extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;
		return ShortValueConverter.JANDEX_SHORT_VALUE_WRAPPER.convert( jandexValue, modelsContext );
	}

}
