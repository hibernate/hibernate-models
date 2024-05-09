/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.util.List;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueConverter;

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
	protected V[] extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;

		final List<AnnotationValue> values = jandexValue.asArrayList();
		assert values != null;

		return wrapper.convert( jandexValue, buildingContext );
	}
}
