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

import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.AnnotationProxy;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.MutableAnnotationUsage;

/**
 * AnnotationUsage built dynamically (for dynamic models, XML mappings, etc.)
 *
 * @author Steve Ebersole
 */
public class DynamicAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final AnnotationDescriptor<A> annotationDescriptor;
	private final AnnotationTarget target;

	private Map<String,Object> values;

	public DynamicAnnotationUsage(AnnotationDescriptor<A> annotationDescriptor) {
		this( annotationDescriptor, null );
	}

	public DynamicAnnotationUsage(AnnotationDescriptor<A> annotationDescriptor, AnnotationTarget target) {
		this.annotationDescriptor = annotationDescriptor;
		this.target = target;

		this.values = extractBaselineValues( annotationDescriptor );
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
		final Object value = findAttributeValue( name );
		if ( value == null ) {
			// null values are not supported as annotation attribute values; we honor that
			// in hibernate-models.  return the default.
			//noinspection unchecked
			return (V) getAnnotationDescriptor().getAttribute( name ).getAttributeMethod().getDefaultValue();
		}
		//noinspection unchecked
		return (V) value;
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
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

	private static <A extends Annotation> Map<String, Object> extractBaselineValues(AnnotationDescriptor<A> annotationDescriptor) {
		final HashMap<String, Object> values = new HashMap<>();
		for ( AttributeDescriptor<?> attribute : annotationDescriptor.getAttributes() ) {
			values.put( attribute.getName(), attribute.getAttributeMethod().getDefaultValue() );
		}
		return values;
	}
}
