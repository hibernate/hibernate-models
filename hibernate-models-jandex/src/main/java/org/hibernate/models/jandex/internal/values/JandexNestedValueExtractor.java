/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.ModelsContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class JandexNestedValueExtractor<A extends Annotation> extends AbstractValueExtractor<A> {
	private final JandexNestedValueConverter<A> wrapper;

	public JandexNestedValueExtractor(JandexNestedValueConverter<A> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected A extractAndWrap(
			AnnotationValue jandexValue,
			ModelsContext modelsContext) {
		return wrapper.convert( jandexValue, modelsContext );
	}
}
