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
public class EnumValueExtractor<E extends Enum<E>> extends AbstractValueExtractor<E> {
	private final EnumValueConverter<E> wrapper;

	public EnumValueExtractor(EnumValueConverter<E> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected E extractAndWrap(AnnotationValue jandexValue, ModelsContext modelsContext) {
		assert jandexValue != null;
		return wrapper.convert( jandexValue, modelsContext );
	}
}
