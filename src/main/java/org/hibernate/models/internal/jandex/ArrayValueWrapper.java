/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class ArrayValueWrapper<V> implements ValueWrapper<List<V>,AnnotationValue> {
	private final ValueWrapper<V,AnnotationValue> elementWrapper;

	public ArrayValueWrapper(ValueWrapper<V, AnnotationValue> elementWrapper) {
		this.elementWrapper = elementWrapper;
	}

	@Override
	public List<V> wrap(AnnotationValue rawValue, SourceModelBuildingContext buildingContext) {
		assert rawValue != null;

		final List<AnnotationValue> values = rawValue.asArrayList();
		assert values != null;

		final List<V> result = new ArrayList<>( values.size() );
		for ( final AnnotationValue value : values ) {
			result.add( elementWrapper.wrap( value, buildingContext ) );
		}
		return result;
	}
}
