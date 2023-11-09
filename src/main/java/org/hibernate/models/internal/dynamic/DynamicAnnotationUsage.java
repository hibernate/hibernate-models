/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.internal.MutableAnnotationUsage;
import org.hibernate.models.spi.AnnotationTarget;

/**
 * @author Steve Ebersole
 */
public class DynamicAnnotationUsage<A extends Annotation> implements MutableAnnotationUsage<A> {
	private final Class<A> annotationType;
	private final AnnotationTarget target;

	private Map<String,Object> values;

	public DynamicAnnotationUsage(Class<A> annotationType) {
		this( annotationType, null );
	}

	public DynamicAnnotationUsage(Class<A> annotationType, AnnotationTarget target) {
		this.annotationType = annotationType;
		this.target = target;
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public AnnotationTarget getAnnotationTarget() {
		return target;
	}

	@Override
	public <V> V getAttributeValue(String name) {
		//noinspection unchecked
		return values == null ? null : (V) values.get( name );
	}

	@Override
	public <V> V setAttributeValue(String name, V value) {
		if ( values == null ) {
			values = new HashMap<>();
		}

		//noinspection unchecked
		return (V) values.put( name, value );
	}
}
