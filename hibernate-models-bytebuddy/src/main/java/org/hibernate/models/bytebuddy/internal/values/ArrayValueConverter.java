/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import java.lang.annotation.Annotation;

import org.hibernate.models.bytebuddy.internal.ByteBuddyBuilders;
import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.internal.ArrayTypeDescriptor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for converting array values
 *
 * @author Steve Ebersole
 */
public class ArrayValueConverter<V> implements ValueConverter<Object> {
	private final Class<?> arrayType;
	private final ValueTypeDescriptor<V> elementTypeDescriptor;

	public ArrayValueConverter(ArrayTypeDescriptor<V> arrayTypeDescriptor) {
		this.arrayType = arrayTypeDescriptor.getValueType();
		this.elementTypeDescriptor = arrayTypeDescriptor.getElementTypeDescriptor();
	}

	@Override
	public Object convert(AnnotationValue<?,?> annotationValue, ModelsContext modelContext) {
		assert annotationValue != null;

		final Class<?> elementValueType = elementTypeDescriptor.getValueType();

		if ( elementValueType.isAnnotation() ) {
			return convertNestedAnnotationArray( annotationValue, modelContext );
		}

		return annotationValue.resolve( arrayType );
	}

	private V[] convertNestedAnnotationArray(
			AnnotationValue<?, ?> annotationValue,
			ModelsContext modelContext) {
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
