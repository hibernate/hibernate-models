/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import java.util.List;

import org.hibernate.models.jandex.spi.JandexModelContext;
import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class ArrayValueConverter<V> implements JandexValueConverter<V[]> {
	private final ValueTypeDescriptor<V> elementTypeDescriptor;

	public ArrayValueConverter(ValueTypeDescriptor<V> elementTypeDescriptor) {
		this.elementTypeDescriptor = elementTypeDescriptor;
	}

	@Override
	public V[] convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext) {
		assert jandexValue != null;

		final List<AnnotationValue> values = jandexValue.asArrayList();
		assert values != null;

		final V[] result = elementTypeDescriptor.makeArray( values.size(), modelContext );
		final JandexValueConverter<V> elementWrapper = modelContext.as( JandexModelContext.class ).getJandexValueConverter( elementTypeDescriptor );
		for ( int i = 0; i < values.size(); i++ ) {
			result[i] = elementWrapper.convert( values.get( i ), modelContext );
		}
		return result;
	}
}
