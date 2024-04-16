/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.internal.AnnotationProxy;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.MutableAnnotationUsage;
import org.hibernate.models.spi.SourceModelContext;

/**
 * AnnotationUsage built dynamically (for dynamic models, XML mappings, etc.)
 *
 * @author Steve Ebersole
 */
public class DynamicAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final AnnotationDescriptor<A> annotationDescriptor;

	private Map<String, Object> values;

	public DynamicAnnotationUsage( AnnotationDescriptor<A> annotationDescriptor, SourceModelContext context) {
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
			SourceModelContext context) {
		final HashMap<String, Object> values = new HashMap<>();
		for ( AttributeDescriptor<?> attribute : annotationDescriptor.getAttributes() ) {
			values.put(
					attribute.getName(),
					extractDefaultValue( attribute.getAttributeMethod().getDefaultValue(), context )
			);
		}
		return values;
	}

	private static Object extractDefaultValue(Object value, SourceModelContext context) {
		if ( value != null ) {
			if ( value.getClass().isArray() ) {
				return extractList( value, context );
			}
			else if ( value instanceof Class<?> clazz ) {
				return context.getClassDetailsRegistry().resolveClassDetails( clazz.getName() );
			}
			else if ( value instanceof Annotation annotation ) {
				try {
					return extractAnnotation( annotation, context );
				}
				catch (InvocationTargetException | IllegalAccessException e) {
					throw new AnnotationAccessException( "Error accessing default annotation-typed attribute", e );
				}
			}
		}
		return value;
	}

	private static <E> List<Object> extractList(Object value, SourceModelContext context) {
		final List<Object> result = new ArrayList<>();
		//noinspection unchecked
		final E[] array = (E[]) value;
		for ( E element : array ) {
			result.add( extractDefaultValue( element, context ) );
		}
		return result;
	}

	private static DynamicAnnotationUsage<?> extractAnnotation(Annotation annotation, SourceModelContext context)
			throws InvocationTargetException, IllegalAccessException {
		final Class<? extends Annotation> annotationType = annotation.annotationType();
		final AnnotationDescriptor<?> descriptor = context.getAnnotationDescriptorRegistry()
				.getDescriptor( annotationType );
		final Map<String, Object> values = new HashMap<>();
		for ( AttributeDescriptor<?> attribute : descriptor.getAttributes() ) {
			values.put(
					attribute.getName(),
					extractDefaultValue( attribute.getAttributeMethod().invoke( annotation ), context )
			);
		}
		return new DynamicAnnotationUsage<>( descriptor, values );
	}
}
