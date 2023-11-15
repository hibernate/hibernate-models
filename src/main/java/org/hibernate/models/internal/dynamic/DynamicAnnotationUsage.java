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
import org.hibernate.models.internal.MutableAnnotationUsage;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;

/**
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
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationDescriptor.getAnnotationType();
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return target;
	}

	@Override
	public <V> V getAttributeValue(String name) {
		if ( values == null ) {
			return null;
		}

		//noinspection unchecked
		final V value = (V) values.get( name );
		if ( value == null ) {
			// this is unusual...
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
		}
		return value;
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		// for set, we need to check up front -
		// todo : do we want to add a distinction for a checked versus unchecked set?
		//		- i.e. setAttributeValueSafely
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
}
