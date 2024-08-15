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

import org.hibernate.models.UnhandledMethodException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.MutableAnnotationDescriptor;

/**
 * Acts as {@link Annotation} usage using {@link InvocationHandler} and {@link Proxy}.
 *
 * @author Steve Ebersole
 */
public class AnnotationProxy<A extends Annotation> implements InvocationHandler {
	private final AnnotationDescriptor<A> annotationDescriptor;
	private final Map<String,Object> valueMap;

	public AnnotationProxy(AnnotationDescriptor<A> annotationDescriptor, Map<String,Object> valueMap) {
		this.annotationDescriptor = annotationDescriptor;
		this.valueMap = valueMap;
	}

	public void setValue(String name, Object value) {
		valueMap.put( name, value );
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

		if ( method.getParameterCount() == 0 ) {
			final AttributeDescriptor<Object> attributeDescriptor = annotationDescriptor.getAttribute( method.getName() );
			return attributeDescriptor.getTypeDescriptor().unwrap( valueMap.get( method.getName() ) );
		}

		// allow for mutability
		if ( isSetValueMethod( method) ) {
			assert method.getParameterCount() == 2;
			valueMap.put( (String) args[0], args[1] );
		}
		if ( method.getParameterCount() == 1 ) {
			valueMap.put( method.getName(), args[0] );
			return null;
		}

		throw new UnhandledMethodException( "Unhandled method - " + method.toGenericString() );
	}

	private boolean isSetValueMethod(Method method) {
		if ( !"setValue".equals( method.getName() ) || method.getParameterCount() != 2 ) {
			return false;
		}

		return String.class.equals( method.getParameterTypes()[0] );
	}

	public static <A extends Annotation> A makeProxy(
			AnnotationDescriptor<A> descriptor,
			Map<String,Object> valueMap) {
		final AnnotationProxy<A> handler = new AnnotationProxy<>( descriptor, valueMap );
		final Class<?>[] interfaces;
		if ( descriptor instanceof MutableAnnotationDescriptor ) {
			//noinspection rawtypes
			interfaces = new Class<?>[] { descriptor.getAnnotationType(), ( (MutableAnnotationDescriptor) descriptor ).getMutableAnnotationType() };
		}
		else {
			interfaces = new Class<?>[] { descriptor.getAnnotationType() };
		}

		//noinspection unchecked
		return (A) Proxy.newProxyInstance(
				AnnotationProxy.class.getClassLoader(),
				interfaces,
				handler
		);
	}
}
