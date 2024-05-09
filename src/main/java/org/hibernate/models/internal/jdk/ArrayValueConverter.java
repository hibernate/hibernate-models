/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

/**
 * @author Steve Ebersole
 */
public class ArrayValueConverter<V> implements JdkValueConverter<V[]> {
	private final ValueTypeDescriptor<V> elementTypeDescriptor;

	public ArrayValueConverter(ValueTypeDescriptor<V> elementTypeDescriptor) {
		this.elementTypeDescriptor = elementTypeDescriptor;
	}

	@Override
	public V[] convert(V[] rawValue, SourceModelBuildingContext modelContext) {
		final V[] result = elementTypeDescriptor.makeArray( rawValue.length, modelContext );
		final JdkValueConverter<V> elementWrapper = elementTypeDescriptor.createJdkValueConverter( modelContext );
		for ( int i = 0; i < rawValue.length; i++ ) {
			result[i] = elementWrapper.convert( rawValue[i], modelContext );
		}
		return result;
	}
}
