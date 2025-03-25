/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;


import java.lang.annotation.Annotation;

import org.hibernate.models.bytebuddy.internal.ByteBuddyBuilders;
import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.description.annotation.AnnotationDescription;
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
	public V[] convert(AnnotationValue<?,?> annotationValue, SourceModelBuildingContext modelContext) {
		assert annotationValue != null;

		final Class<?> elementValueType = elementTypeDescriptor.getValueType();

		if ( Boolean.class == elementValueType ) {
			return convertBooleanArray( annotationValue, modelContext );
		}

		if ( Byte.class == elementValueType ) {
			return convertByteArray( annotationValue, modelContext );
		}

		if ( Short.class == elementValueType ) {
			return convertShortArray( annotationValue, modelContext );
		}

		if ( Integer.class == elementValueType ) {
			return convertIntArray( annotationValue, modelContext );
		}

		if ( Long.class == elementValueType ) {
			return convertLongArray( annotationValue, modelContext );
		}

		if ( double.class == elementValueType ) {
			return convertDoubleArray( annotationValue, modelContext );
		}

		if ( float.class == elementValueType ) {
			return convertFloatArray( annotationValue, modelContext );
		}

		if ( Character.class == elementValueType ) {
			return convertCharacterArray( annotationValue, modelContext );
		}

		if ( elementValueType.isAnnotation() ) {
			return convertNestedAnnotationArray( annotationValue, modelContext );
		}

		final Class<?> arrayType = elementValueType.arrayType();
		//noinspection unchecked
		return (V[]) annotationValue.resolve( arrayType );
	}

	private V[] convertBooleanArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final boolean[] resolved = annotationValue.resolve( boolean[].class );
		final Boolean[] result = (Boolean[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertByteArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final byte[] resolved = annotationValue.resolve( byte[].class );
		final Byte[] result = (Byte[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertShortArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final short[] resolved = annotationValue.resolve( short[].class );
		final Short[] result = (Short[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertIntArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final int[] resolved = annotationValue.resolve( int[].class );
		final Integer[] result = (Integer[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertLongArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final long[] resolved = annotationValue.resolve( long[].class );
		final Long[] result = (Long[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertDoubleArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final double[] resolved = annotationValue.resolve( double[].class );
		final Double[] result = (Double[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertFloatArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final float[] resolved = annotationValue.resolve( float[].class );
		final Float[] result = (Float[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertCharacterArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final char[] resolved = annotationValue.resolve( char[].class );
		final Character[] result = (Character[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );
		for ( int i = 0; i < resolved.length; i++ ) {
			result[i] = resolved[i];
		}
		//noinspection unchecked
		return (V[]) result;
	}

	private V[] convertNestedAnnotationArray(
			AnnotationValue<?, ?> annotationValue,
			SourceModelBuildingContext modelContext) {
		final AnnotationDescriptorRegistry descriptorRegistry = modelContext.getAnnotationDescriptorRegistry();

		//noinspection unchecked
		final Class<? extends Annotation> annotationType = (Class<? extends Annotation>) elementTypeDescriptor.getValueType();
		final AnnotationDescriptor<? extends Annotation> annotationDescriptor = descriptorRegistry.getDescriptor( annotationType );

		final AnnotationDescription[] resolved = annotationValue.resolve( AnnotationDescription[].class );
		final Annotation[] result = (Annotation[]) elementTypeDescriptor.makeArray( resolved.length, modelContext );

		for ( int i = 0; i < resolved.length; i++ ) {
			final AnnotationDescription annotationDescription = resolved[i];
			final Annotation usage = ByteBuddyBuilders.makeUsage(
					annotationDescription,
					annotationDescriptor,
					modelContext
			);
			result[i] = usage;
		}

		//noinspection unchecked
		return (V[]) result;
	}
}
