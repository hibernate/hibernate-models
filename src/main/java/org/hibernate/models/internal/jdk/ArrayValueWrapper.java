/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

/**
 * @author Steve Ebersole
 */
public class ArrayValueWrapper<V,R> implements ValueWrapper<List<V>,R[]> {
	private final ValueWrapper<V,R> elementWrapper;

	public ArrayValueWrapper(ValueWrapper<V, R> elementWrapper) {
		this.elementWrapper = elementWrapper;
	}

	@Override
	public List<V> wrap(R[] rawValues, SourceModelBuildingContext buildingContext) {
		final List<V> result = new ArrayList<>( rawValues.length );
		for ( final R rawValue : rawValues ) {
			result.add( elementWrapper.wrap( rawValue, buildingContext ) );
		}
		return result;
	}
}
