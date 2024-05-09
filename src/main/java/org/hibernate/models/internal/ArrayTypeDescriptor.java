/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.List;

import org.hibernate.models.internal.jandex.ArrayValueExtractor;
import org.hibernate.models.internal.jandex.ArrayValueConverter;
import org.hibernate.models.internal.jdk.PassThruConverter;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueExtractor;
import org.hibernate.models.spi.ValueTypeDescriptor;
import org.hibernate.models.spi.JandexValueConverter;

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

	private JandexValueConverter<V[]> jandexValueConverter;
	private JandexValueExtractor<V[]> jandexValueExtractor;

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
	public JandexValueExtractor<V[]> createJandexValueExtractor(SourceModelBuildingContext buildingContext) {
		return resolveJandexExtractor( buildingContext );
	}

	public JandexValueExtractor<V[]> resolveJandexExtractor(SourceModelBuildingContext buildingContext) {
		if ( jandexValueExtractor == null ) {
			this.jandexValueExtractor = new ArrayValueExtractor<>( resolveJandexWrapper( buildingContext ) );
		}
		return jandexValueExtractor;
	}

	@Override
	public JandexValueConverter<V[]> createJandexValueConverter(SourceModelBuildingContext buildingContext) {
		return resolveJandexWrapper( buildingContext );
	}

	private JandexValueConverter<V[]> resolveJandexWrapper(SourceModelBuildingContext buildingContext) {
		if ( jandexValueConverter == null ) {
			jandexValueConverter = new ArrayValueConverter<>( elementTypeDescriptor );
		}

		return jandexValueConverter;
	}

	@Override
	public JdkValueConverter<V[]> createJdkValueConverter(SourceModelBuildingContext modelContext) {
		if ( jdkValueConverter == null ) {
			if ( !elementTypeDescriptor.getValueType().isAnnotation() ) {
				// for arrays of anything other than nested annotations we can simply return the raw array
				jdkValueConverter = PassThruConverter.passThruConverter();
			}
			else {
				jdkValueConverter = new org.hibernate.models.internal.jdk.ArrayValueConverter<>( elementTypeDescriptor );
			}
		}

		return jdkValueConverter;
	}

	@Override
	public JdkValueExtractor<V[]> createJdkValueExtractor(SourceModelBuildingContext modelContext) {
		if ( jdkValueExtractor == null ) {
			if ( !elementTypeDescriptor.getValueType().isAnnotation() ) {
				// for arrays of anything other than nested annotations we can simply return the raw array
				jdkValueExtractor = PassThruExtractor.passThruExtractor();
			}
			else {
				jdkValueExtractor = new org.hibernate.models.internal.jdk.ArrayValueExtractor<>( createJdkValueConverter( modelContext ) );
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
	public void render(RenderingCollector collector, String name, Object attributeValue, SourceModelBuildingContext modelContext) {
		assert attributeValue != null : "Annotation value was null - " + name;

		//noinspection unchecked
		final V[] values = (V[]) attributeValue;

		collector.addLine( "%s = {", name );
		collector.indent( 2 );
		for ( V value : values ) {
			elementTypeDescriptor.render( collector, value, modelContext );
		}
		collector.unindent( 2 );
		collector.addLine( "}" );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		final List<V> values = (List<V>) attributeValue;

		collector.addLine( "{" );
		collector.indent( 2 );
		values.forEach( (value) -> elementTypeDescriptor.render( collector, value, modelContext ) );
		collector.unindent( 2 );
		collector.addLine( "}" );
	}

	@Override
	public V[][] makeArray(int size, SourceModelBuildingContext modelContext) {
		throw new UnsupportedOperationException( "Nested array creation not supported" );
	}
}
