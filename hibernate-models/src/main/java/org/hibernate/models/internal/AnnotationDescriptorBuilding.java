/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.internal.util.AnnotationAttributeTypeHelper;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ValueTypeDescriptor;

/**
 * @author Steve Ebersole
 */
public class AnnotationDescriptorBuilding {

	public static <A extends Annotation> List<AttributeDescriptor<?>> extractAttributeDescriptors(Class<A> annotationType) {
		final Method[] methods = annotationType.getDeclaredMethods();
		final List<AttributeDescriptor<?>> attributeDescriptors = new ArrayList<>( methods.length );
		for ( Method method : methods ) {
			attributeDescriptors.add( createAttributeDescriptor( annotationType, method ) );
		}
		return attributeDescriptors;
	}

	private static <X, A extends Annotation> AttributeDescriptor<X> createAttributeDescriptor(
			Class<A> annotationType,
			Method method) {
		//noinspection unchecked
		final Class<X> attributeType = (Class<X>) method.getReturnType();

		final ValueTypeDescriptor<X> typeDescriptor = AnnotationAttributeTypeHelper.resolveTypeDescriptor( attributeType );
		return typeDescriptor.createAttributeDescriptor( annotationType, method.getName() );
	}
}
