/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import java.lang.reflect.Array;
import java.util.List;

import org.hibernate.models.internal.ArrayTypeDescriptor;
import org.hibernate.models.jandex.spi.JandexModelsContext;
import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class ArrayValueConverter<V> implements JandexValueConverter<Object> {
	private final Class<?> componentType;
	private final ValueTypeDescriptor<V> elementTypeDescriptor;

	public ArrayValueConverter(ArrayTypeDescriptor<V> arrayTypeDescriptor) {
		this.componentType = arrayTypeDescriptor.getValueType().getComponentType();
		this.elementTypeDescriptor = arrayTypeDescriptor.getElementTypeDescriptor();
	}

	@Override
	public Object convert(AnnotationValue jandexValue, ModelsContext modelContext) {
		assert jandexValue != null;

		final List<AnnotationValue> values = jandexValue.asArrayList();
		assert values != null;

		final Object result = Array.newInstance( componentType, values.size() );
		final JandexValueConverter<V> elementWrapper = modelContext.as( JandexModelsContext.class ).getJandexValueConverter( elementTypeDescriptor );
		for ( int i = 0; i < values.size(); i++ ) {
			Array.set( result, i, elementWrapper.convert( values.get( i ), modelContext ) );
		}
		return result;
	}
}
