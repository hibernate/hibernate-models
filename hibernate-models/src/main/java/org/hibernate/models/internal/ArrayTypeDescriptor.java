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

import org.hibernate.models.internal.jdk.JdkArrayValueConverter;
import org.hibernate.models.internal.jdk.JdkArrayValueExtractor;
import org.hibernate.models.internal.jdk.JdkPassThruConverter;
import org.hibernate.models.internal.jdk.JdkPassThruExtractor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.rendering.spi.Renderer;
import org.hibernate.models.rendering.spi.RenderingTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;
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
	public JdkValueConverter<V[]> createJdkValueConverter(SourceModelBuildingContext modelContext) {
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
	public JdkValueExtractor<V[]> createJdkValueExtractor(SourceModelBuildingContext modelContext) {
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
	public void render(
			String name,
			Object attributeValue,
			RenderingTarget target,
			Renderer renderer,
			SourceModelContext modelContext) {
		assert attributeValue != null : "Annotation value was null - " + name;

		//noinspection unchecked
		final V[] values = (V[]) attributeValue;

		target.addLine( "%s = {", name );
		target.indent( 2 );
		for ( V value : values ) {
			elementTypeDescriptor.render( value, target, renderer, modelContext );
		}
		target.unindent( 2 );
		target.addLine( "}" );
	}

	@Override
	public void render(Object attributeValue, RenderingTarget target, Renderer renderer, SourceModelContext modelContext) {
		//noinspection unchecked
		final List<V> values = (List<V>) attributeValue;

		target.addLine( "{" );
		target.indent( 2 );
		values.forEach( (value) -> elementTypeDescriptor.render( value, target, renderer, modelContext ) );
		target.unindent( 2 );
		target.addLine( "}" );
	}

	@Override
	public V[][] makeArray(int size, SourceModelBuildingContext modelContext) {
		throw new UnsupportedOperationException( "Nested array creation not supported" );
	}
}
