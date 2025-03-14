/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting array values
 *
 * @author Steve Ebersole
 */
public class ArrayValueConverter<V> implements ValueConverter<V[]> {
	private final ValueTypeDescriptor<V> elementTypeDescriptor;

	public ArrayValueConverter(ValueTypeDescriptor<V> elementTypeDescriptor) {
		this.elementTypeDescriptor = elementTypeDescriptor;
	}

	@Override
	public V[] convert(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext modelContext) {
		assert byteBuddyValue != null;

		//noinspection unchecked
		return (V[]) byteBuddyValue.resolve();
	}
}
