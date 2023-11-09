/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.util.List;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class ArrayValueExtractor<V> extends AbstractValueExtractor<List<V>> {
	private final ValueWrapper<List<V>,AnnotationValue> wrapper;

	public ArrayValueExtractor(ValueWrapper<List<V>,AnnotationValue> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected List<V> extractAndWrap(
			AnnotationValue jandexValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;

		final List<AnnotationValue> values = jandexValue.asArrayList();
		assert values != null;

		return wrapper.wrap( jandexValue, target, buildingContext );
	}
}
