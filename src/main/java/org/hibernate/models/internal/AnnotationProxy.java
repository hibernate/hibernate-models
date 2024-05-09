/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;

import org.hibernate.models.internal.jandex.AnnotationUsageBuilder;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * Acts as {@link Annotation} usage using {@link InvocationHandler} and {@link Proxy}.
 *
 * @apiNote This should only be used for annotations not known ahead of time (user annotations)
 *
 * @author Steve Ebersole
 */
public class AnnotationProxy<A extends Annotation> implements InvocationHandler {
	private final AnnotationDescriptor<A> annotationDescriptor;
	private final Map<String,?> valueMap;

	public AnnotationProxy(AnnotationDescriptor<A> annotationDescriptor, Map<String, ?> valueMap) {
		this.annotationDescriptor = annotationDescriptor;
		this.valueMap = valueMap;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) {
		switch ( method.getName() ) {
			case "annotationType" -> {
				return annotationDescriptor.getAnnotationType();
			}
			case "toString" -> {
				return "AnnotationProxy(" + annotationDescriptor.getAnnotationType().getName() + ")";
			}
			case "equals" -> {
				return proxy == this;
			}
		}

		final AttributeDescriptor<Object> attributeDescriptor = annotationDescriptor.getAttribute( method.getName() );
		return attributeDescriptor.getTypeDescriptor().unwrap( valueMap.get( method.getName() ) );
	}

	public static <A extends Annotation> A makeProxy(
			AnnotationDescriptor<A> descriptor,
			Map<String,?> valueMap) {
		final AnnotationProxy<A> handler = new AnnotationProxy<>( descriptor, valueMap );
		//noinspection unchecked
		return (A) Proxy.newProxyInstance(
				AnnotationProxy.class.getClassLoader(),
				new Class<?>[] { descriptor.getAnnotationType() },
				handler
		);
	}

	public static <A extends Annotation> A makeProxy(
			AnnotationDescriptor<A> descriptor,
			AnnotationInstance jandexAnnotationInstance,
			SourceModelBuildingContext modelContext) {
		return makeProxy(
				descriptor,
				AnnotationUsageBuilder.extractAttributeValues(
						jandexAnnotationInstance,
						descriptor,
						modelContext
				)
		);
	}

}
