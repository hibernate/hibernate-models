/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import java.lang.reflect.Field;
import java.util.List;

import org.hibernate.models.ModelsException;
import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.description.annotation.AnnotationValue;
import net.bytebuddy.pool.TypePool;

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

		// UGH...
		final List<AnnotationValue<?,?>> byteBuddyValues = extractValueValues( byteBuddyValue, modelContext );

		final V[] result = elementTypeDescriptor.makeArray( byteBuddyValues.size(), modelContext );
		final ValueConverter<V> elementWrapper = modelContext.as( ByteBuddyModelsContext.class ).getValueConverter( elementTypeDescriptor );

		for ( int i = 0; i < byteBuddyValues.size(); i++ ) {
			result[i] = elementWrapper.convert( byteBuddyValues.get(i), modelContext );
		}
		return result;
	}

	private List<AnnotationValue<?, ?>> extractValueValues(
			AnnotationValue<?, ?> byteBuddyValue,
			SourceModelBuildingContext modelContext) {
		try {
			//noinspection unchecked
			return (List<AnnotationValue<?,?>>) getValuesField( modelContext ).get( byteBuddyValue );
		}
		catch (IllegalAccessException e) {
			throw new ModelsException( "Could not access Byte Buddy's `AnnotationValue.ForDescriptionArray#values` field", e );
		}
	}

	private Field getValuesField(SourceModelBuildingContext modelContext) {
		return ARRRRGGGGHHHHH;
	}

	private static Field ARRRRGGGGHHHHH;

	static {
		try {
			ARRRRGGGGHHHHH = AnnotationValue.ForDescriptionArray.class.getDeclaredField( "values" );
			ARRRRGGGGHHHHH.setAccessible( true );
		}
		catch (Exception e) {
			throw new ModelsException( "Could not access Byte Buddy's `AnnotationValue.ForDescriptionArray#values` field", e );
		}
	}
}
