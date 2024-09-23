/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Locale;

import org.hibernate.models.ModelsException;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ValueTypeDescriptor;

/**
 * Standard {@linkplain AttributeDescriptor} implementation
 *
 * @author Steve Ebersole
 */
public class AttributeDescriptorImpl<T> implements AttributeDescriptor<T> {
	private final String name;
	private final Method method;
	private final ValueTypeDescriptor<T> typeDescriptor;

	public AttributeDescriptorImpl(Class<? extends Annotation> annotationType, String name, ValueTypeDescriptor<T> typeDescriptor) {
		this.name = name;
		this.typeDescriptor = typeDescriptor;

		try {
			this.method = annotationType.getDeclaredMethod( name );
		}
		catch (NoSuchMethodException e) {
			throw new ModelsException( "Could not locate annotation attribute method - " + name, e );
		}
	}

	public AttributeDescriptorImpl(Method method, ValueTypeDescriptor<T> typeDescriptor) {
		this.name = method.getName();
		this.method = method;
		this.typeDescriptor = typeDescriptor;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ValueTypeDescriptor<T> getTypeDescriptor() {
		return typeDescriptor;
	}

	@Override
	public Method getAttributeMethod() {
		return method;
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"AttributeDescriptor(%s : %s)",
				name,
				getTypeDescriptor().getValueType().getName()
		);
	}
}
