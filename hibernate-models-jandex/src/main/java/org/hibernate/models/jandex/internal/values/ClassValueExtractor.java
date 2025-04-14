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
public class ClassValueExtractor extends AbstractValueExtractor<Class<?>> {
	public static final ClassValueExtractor JANDEX_CLASS_EXTRACTOR = new ClassValueExtractor();

	@Override
	protected Class<?> extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;
		return ClassValueConverter.JANDEX_CLASS_VALUE_WRAPPER.convert( jandexValue, modelsContext );
	}
}
