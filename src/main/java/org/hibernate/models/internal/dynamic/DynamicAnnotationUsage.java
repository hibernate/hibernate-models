/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.models.internal.AnnotationProxy;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.MutableAnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * AnnotationUsage built dynamically (for dynamic models, XML mappings, etc.)
 *
 * @author Steve Ebersole
 */
public class DynamicAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final AnnotationDescriptor<A> annotationDescriptor;

	private Map<String, Object> values;

	public DynamicAnnotationUsage( AnnotationDescriptor<A> annotationDescriptor, SourceModelBuildingContext context) {
		this( annotationDescriptor, extractBaselineValues( annotationDescriptor, context ) );
	}

	private DynamicAnnotationUsage(AnnotationDescriptor<A> annotationDescriptor, Map<String, Object> values) {
		this.annotationDescriptor = annotationDescriptor;
		this.values = values;
	}

	@Override
	public AnnotationDescriptor<A> getAnnotationDescriptor() {
		return annotationDescriptor;
	}

	@Override
	public A toAnnotation() {
		return AnnotationProxy.makeProxy( annotationDescriptor, values );
	}

	@Override
	public <V> V findAttributeValue(String name) {
		if ( values != null ) {
			//noinspection unchecked
			return (V) values.get( name );
		}

		return null;
	}

	/**
	 * DynamicAnnotationUsage
	 */
	@Override
	public <V> V getAttributeValue(String name) {
		// Validate the attribute exists on the annotation descriptor
		final AttributeDescriptor<V> attribute = annotationDescriptor.getAttribute( name );
		return findAttributeValue( attribute.getName() );
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		if ( value == null ) {
			throw new IllegalArgumentException(
					String.format(
							Locale.ROOT,
							"Null value not allowed for attribute `%s` of annotation `%s`",
							name,
							getAnnotationType().getName()
					)
			);
		}

		// Validate the attribute exists on the annotation descriptor
		final AttributeDescriptor<V> attribute = annotationDescriptor.getAttribute( name );

		if ( values == null ) {
			values = new HashMap<>();
		}

		//noinspection unchecked
		return (V) values.put( attribute.getName(), value );
	}

	private static <A extends Annotation> Map<String, Object> extractBaselineValues(
			AnnotationDescriptor<A> annotationDescriptor,
			SourceModelBuildingContext context) {
		final HashMap<String, Object> values = new HashMap<>();
		for ( AttributeDescriptor<?> attribute : annotationDescriptor.getAttributes() ) {
			values.put(
					attribute.getName(),
					attribute.getTypeDescriptor().createValue( attribute, context )
			);
		}
		return values;
	}
}
