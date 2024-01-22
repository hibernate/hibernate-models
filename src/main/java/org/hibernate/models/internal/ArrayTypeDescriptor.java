/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.jandex.ArrayValueExtractor;
import org.hibernate.models.internal.jandex.ArrayValueWrapper;
import org.hibernate.models.internal.util.ArrayHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueTypeDescriptor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * Descriptor for array values.  These are modeled as an array in the
 * annotation, but as a List here.
 *
 * @author Steve Ebersole
 */
public class ArrayTypeDescriptor<V> implements ValueTypeDescriptor<List<V>> {
	private final ValueTypeDescriptor<V> elementTypeDescriptor;

	private ValueWrapper<List<V>, AnnotationValue> jandexValueWrapper;
	private ValueExtractor<AnnotationInstance,List<V>> jandexValueExtractor;

	private ValueWrapper<List<V>,Object[]> jdkValueWrapper;
	private ValueExtractor<Annotation,List<V>> jdkValueExtractor;

	public ArrayTypeDescriptor(ValueTypeDescriptor<V> elementTypeDescriptor) {
		this.elementTypeDescriptor = elementTypeDescriptor;
	}

	@Override
	public Class<List<V>> getWrappedValueType() {
		//noinspection unchecked,rawtypes
		return (Class) List.class;
	}

	@Override
	public List<V> createValue(
			AttributeDescriptor<?> attributeDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext context) {
		final Object[] defaultValue = (Object[]) attributeDescriptor.getAttributeMethod().getDefaultValue();
		if ( ArrayHelper.isEmpty( defaultValue ) ) {
			return Collections.emptyList();
		}

		return jdkValueWrapper.wrap( defaultValue, target, context );
	}

	@Override
	public AttributeDescriptor<List<V>> createAttributeDescriptor(
			AnnotationDescriptor<?> annotationDescriptor,
			String attributeName) {
		return new AttributeDescriptorImpl<>( annotationDescriptor.getAnnotationType(), attributeName, this );
	}

	@Override
	public ValueExtractor<AnnotationInstance, List<V>> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return resolveJandexExtractor( buildingContext );
	}

	public ValueExtractor<AnnotationInstance, List<V>> resolveJandexExtractor(SourceModelBuildingContext buildingContext) {
		if ( jandexValueExtractor == null ) {
			this.jandexValueExtractor = new ArrayValueExtractor<>( resolveJandexWrapper( buildingContext ) );
		}
		return jandexValueExtractor;
	}

	@Override
	public ValueWrapper<List<V>, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return resolveJandexWrapper( buildingContext );
	}

	private ValueWrapper<List<V>, AnnotationValue> resolveJandexWrapper(SourceModelBuildingContext buildingContext) {
		if ( jandexValueWrapper == null ) {
			final ValueWrapper<V,AnnotationValue> jandexElementWrapper = elementTypeDescriptor.createJandexWrapper( buildingContext );
			jandexValueWrapper = new ArrayValueWrapper<>( jandexElementWrapper );
		}

		return jandexValueWrapper;
	}

	@Override
	public ValueExtractor<Annotation, List<V>> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		return resolveJdkExtractor( buildingContext );
	}

	public ValueExtractor<Annotation, List<V>> resolveJdkExtractor(SourceModelBuildingContext buildingContext) {
		if ( jdkValueExtractor == null ) {
			this.jdkValueExtractor = new org.hibernate.models.internal.jdk.ArrayValueExtractor<>( resolveJkWrapper( buildingContext ) );
		}
		return jdkValueExtractor;
	}

	@Override
	public ValueWrapper<List<V>,Object[]> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		return resolveJkWrapper( buildingContext );
	}

	public ValueWrapper<List<V>,Object[]> resolveJkWrapper(SourceModelBuildingContext buildingContext) {
		if ( jdkValueWrapper == null ) {
			//noinspection unchecked
			final ValueWrapper<V,Object> jdkElementWrapper = (ValueWrapper<V, Object>) elementTypeDescriptor.createJdkWrapper( buildingContext );
			jdkValueWrapper = new org.hibernate.models.internal.jdk.ArrayValueWrapper<>( jdkElementWrapper );
		}
		return jdkValueWrapper;
	}

	@Override
	public Object unwrap(List<V> value) {
		final Object[] result = new Object[value.size()];
		for ( int i = 0; i < value.size(); i++ ) {
			result[i] = elementTypeDescriptor.unwrap( value.get(i) );
		}
		return result;
	}
}
