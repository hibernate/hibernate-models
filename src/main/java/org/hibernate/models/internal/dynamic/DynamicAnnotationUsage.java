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
import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.AnnotationProxy;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
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
	private final AnnotationTarget target;

	private Map<String,Object> values;

	public DynamicAnnotationUsage(
			AnnotationDescriptor<A> annotationDescriptor,
			SourceModelContext context) {
		this( annotationDescriptor, null, context );
	}

	public DynamicAnnotationUsage(
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget target,
			SourceModelContext context) {
		this.annotationDescriptor = annotationDescriptor;
		this.target = target;

		this.values = extractBaselineValues( annotationDescriptor, target, context );
	}

	@Override
	public AnnotationDescriptor<A> getAnnotationDescriptor() {
		return annotationDescriptor;
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return target;
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
		if ( annotationDescriptor.getAttribute( name ) == null ) {
			throw new UnknownAnnotationAttributeException(
					String.format(
							Locale.ROOT,
							"Unknown attribute `%s` for annotation `%s`",
							name,
							getAnnotationType().getName()
					)
			);
		}
		return findAttributeValue( name );
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		if (value == null){
			throw new IllegalArgumentException(
					String.format(
							Locale.ROOT,
							"Null value not allowed for attribute `%s` of annotation `%s`",
							name,
							getAnnotationType().getName()
					)
			);
		}

		if ( annotationDescriptor.getAttribute( name ) == null ) {
			throw new UnknownAnnotationAttributeException(
					String.format(
							Locale.ROOT,
							"Unknown attribute `%s` for annotation `%s`",
							name,
							getAnnotationType().getName()
					)
			);
		}

		if ( values == null ) {
			values = new HashMap<>();
		}

		//noinspection unchecked
		return (V) values.put( name, value );
	}

	private static <A extends Annotation> Map<String, Object> extractBaselineValues(
			AnnotationDescriptor<A> annotationDescriptor,
			AnnotationTarget target,
			SourceModelContext context) {
		final HashMap<String, Object> values = new HashMap<>();
		for ( AttributeDescriptor<?> attribute : annotationDescriptor.getAttributes() ) {
			values.put( attribute.getName(), getDefaultValue( attribute, target, context ) );
		}
		return values;
	}

	private static Object getDefaultValue(
			AttributeDescriptor<?> attribute,
			AnnotationTarget target,
			SourceModelContext context) {
		final Object defaultValue = attribute.getAttributeMethod().getDefaultValue();
		Object annotation = wrapValue( defaultValue, target, context );
		if ( annotation != null ) {
			return annotation;
		}
		return defaultValue;
	}

	private static Object wrapValue(Object value, AnnotationTarget target, SourceModelContext context) {
		if ( value instanceof Annotation annotation ) {
			try {
				return extractDynamicAnnotationUsage( annotation, target, context );
			}
			catch (InvocationTargetException | IllegalAccessException e) {
				throw new AnnotationAccessException( "Error accessing default annotation attribute value", e );
			}
		}
		else if ( value != null ) {
			if ( value.getClass().isArray() ) {
				return getList( value, target, context );
			}
			else if ( value.getClass() == Class.class ) {
				return context.getClassDetailsRegistry().findClassDetails( ( (Class) value ).getName() );
			}
		}

		return value;
	}

	private static <E> List getList(Object defaultValue, AnnotationTarget target, SourceModelContext context) {
		List result = new ArrayList<>();
		E[] d = (E[]) defaultValue;
		for ( E e : d ) {
			result.add( wrapValue( e, target, context ) );
		}
		return result;
	}

	private static DynamicAnnotationUsage<?> extractDynamicAnnotationUsage(
			Annotation annotation,
			AnnotationTarget target,
			SourceModelContext context) throws InvocationTargetException, IllegalAccessException {
		final Class<? extends Annotation> annotationType = annotation.annotationType();
		final AnnotationDescriptor<?> descriptor = context.getAnnotationDescriptorRegistry()
				.getDescriptor( annotationType );
		final DynamicAnnotationUsage<?> annotationUsage = new DynamicAnnotationUsage<>( descriptor, target, context );
		for ( AttributeDescriptor<?> attribute : descriptor.getAttributes() ) {
			annotationUsage.setAttributeValue(
					attribute.getName(),
					attribute.getAttributeMethod().invoke( annotation )
			);
		}
		return annotationUsage;
	}
}
