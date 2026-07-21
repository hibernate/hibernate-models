/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;

import org.hibernate.models.jdk.internal.JdkArrayValueConverter;
import org.hibernate.models.jdk.internal.JdkArrayValueExtractor;
import org.hibernate.models.jdk.internal.JdkPassThruConverter;
import org.hibernate.models.jdk.internal.JdkPassThruExtractor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

/**
 * Descriptor for array values.  These are modeled as an array in the
 * annotation, but as a List here.
 *
 * @author Steve Ebersole
 */
public class ArrayTypeDescriptor<V> implements ValueTypeDescriptor<V[]> {
	private final ValueTypeDescriptor<V> elementTypeDescriptor;
	private final Class<V> componentType;
	private final Class<V[]> arrayType;

	private JdkValueConverter<V[]> jdkValueConverter;
	private JdkValueExtractor<V[]> jdkValueExtractor;

	public ArrayTypeDescriptor(ValueTypeDescriptor<V> elementTypeDescriptor) {
		this.elementTypeDescriptor = elementTypeDescriptor;
		this.componentType = elementTypeDescriptor.getValueType();
		//noinspection unchecked
		this.arrayType = (Class<V[]>) componentType.arrayType();
	}

	public ValueTypeDescriptor<V> getElementTypeDescriptor() {
		return elementTypeDescriptor;
	}

	@Override
	public Class<V[]> getValueType() {
		return arrayType;
	}

	@Override
	public AttributeDescriptor<V[]> createAttributeDescriptor(
			Class<? extends Annotation> annotationType,
			String attributeName) {
		return new AttributeDescriptorImpl<>( annotationType, attributeName, this );
	}

	@Override
	public JdkValueConverter<V[]> createJdkValueConverter(ModelsContext modelContext) {
		if ( jdkValueConverter == null ) {
			if ( !elementTypeDescriptor.getValueType().isAnnotation() ) {
				// for arrays of anything other than nested annotations we can simply return the raw array
				jdkValueConverter = JdkPassThruConverter.passThruConverter();
			}
			else {
				jdkValueConverter = new JdkArrayValueConverter<>( elementTypeDescriptor );
			}
		}

		return jdkValueConverter;
	}

	@Override
	public JdkValueExtractor<V[]> createJdkValueExtractor(ModelsContext modelContext) {
		if ( jdkValueExtractor == null ) {
			if ( !elementTypeDescriptor.getValueType().isAnnotation() ) {
				// for arrays of anything other than nested annotations we can simply return the raw array
				jdkValueExtractor = JdkPassThruExtractor.passThruExtractor();
			}
			else {
				jdkValueExtractor = new JdkArrayValueExtractor<>( createJdkValueConverter( modelContext ) );
			}
		}
		return jdkValueExtractor;
	}

	@Override
	public Object unwrap(V[] value) {
		final Object[] result = (Object[]) Array.newInstance( componentType, value.length );
		for ( int i = 0; i < value.length; i++ ) {
			result[i] = elementTypeDescriptor.unwrap( value[i] );
		}
		return result;
	}

	@Override
	public V[][] makeArray(int size, ModelsContext modelContext) {
		throw new UnsupportedOperationException( "Nested array creation not supported" );
	}
}
