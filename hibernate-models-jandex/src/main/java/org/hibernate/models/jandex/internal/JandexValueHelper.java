/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.jandex.spi.JandexModelContext;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class JandexValueHelper {
	public static <A extends Annotation, V> V extractValue(
			AnnotationInstance usage,
			AnnotationDescriptor<A> descriptor,
			String attributeName,
			SourceModelBuildingContext modelContext) {
		final AttributeDescriptor<V> attribute = descriptor.getAttribute( attributeName );
		return extractValue( usage, attribute, modelContext );
	}

	public static <V> V extractValue(
			AnnotationInstance usage,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext modelContext) {
		return modelContext.as( JandexModelContext.class )
				.getJandexValueExtractor( attributeDescriptor.getTypeDescriptor() )
				.extractValue( usage, attributeDescriptor, modelContext );
//		final AnnotationValue value = usage.value( attributeDescriptor.getName() );
//		if ( value == null ) {
//			//noinspection unchecked
//			return (V) attributeDescriptor.getAttributeMethod().getDefaultValue();
//		}
//
//		return attributeDescriptor
//				.getTypeDescriptor()
//				.createValueConverter( modelContext )
//				.convert( value, modelContext );
	}

	public static <A extends Annotation, V> V extractOptionalValue(
			AnnotationInstance usage,
			AnnotationDescriptor<A> descriptor,
			String attributeName,
			SourceModelBuildingContext modelContext) {
		return extractOptionalValue( usage, descriptor.getAttribute( attributeName ), modelContext );
	}

	public static <V> V extractOptionalValue(
			AnnotationInstance usage,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext modelContext) {
		final AnnotationValue value = usage.value( attributeDescriptor.getName() );
		if ( value == null ) {
			//noinspection unchecked
			return (V) attributeDescriptor.getAttributeMethod().getDefaultValue();
		}

		return modelContext.as( JandexModelContext.class )
				.getJandexValueExtractor( attributeDescriptor.getTypeDescriptor() )
				.extractValue( usage, attributeDescriptor, modelContext );
	}
}
