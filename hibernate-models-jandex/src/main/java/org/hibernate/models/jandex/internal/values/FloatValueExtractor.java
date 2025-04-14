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
public class FloatValueExtractor extends AbstractValueExtractor<Float> {
	public static final FloatValueExtractor JANDEX_FLOAT_EXTRACTOR = new FloatValueExtractor();

	@Override
	protected Float extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;
		return FloatValueConverter.JANDEX_FLOAT_VALUE_WRAPPER.convert( jandexValue, modelsContext );
	}
}
