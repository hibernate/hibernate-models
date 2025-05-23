/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import java.util.List;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class ArrayValueExtractor<V> extends AbstractValueExtractor<V[]> {
	private final JandexValueConverter<V[]> wrapper;

	public ArrayValueExtractor(JandexValueConverter<V[]> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected V[] extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;

		final List<AnnotationValue> values = jandexValue.asArrayList();
		assert values != null;

		return wrapper.convert( jandexValue, modelsContext );
	}
}
