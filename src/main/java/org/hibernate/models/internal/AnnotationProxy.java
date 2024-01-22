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

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;

/**
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

}
