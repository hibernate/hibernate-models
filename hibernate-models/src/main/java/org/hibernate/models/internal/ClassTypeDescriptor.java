/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for class values
 *
 * @author Steve Ebersole
 */
public class ClassTypeDescriptor extends AbstractTypeDescriptor<Class<?>> {
	public static final ClassTypeDescriptor CLASS_TYPE_DESCRIPTOR = new ClassTypeDescriptor();

	@Override
	public Class<Class<?>> getValueType() {
		//noinspection unchecked,rawtypes
		return (Class) Class.class;
	}

	@Override
	public Object unwrap(Class<?> value) {
		return value;
	}

	@Override
	public Class<?>[] makeArray(int size, ModelsContext modelContext) {
		return new Class<?>[size];
	}
}
