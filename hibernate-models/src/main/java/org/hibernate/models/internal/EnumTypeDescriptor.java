/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.reflect.Array;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for enum values
 *
 * @author Steve Ebersole
 */
public class EnumTypeDescriptor<E extends Enum<E>> extends AbstractTypeDescriptor<E> {
	private final Class<E> enumType;

	public EnumTypeDescriptor(Class<E> enumType) {
		this.enumType = enumType;
	}

	@Override
	public Class<E> getValueType() {
		return enumType;
	}

	@Override
	public Object unwrap(E value) {
		return value;
	}

	@Override
	public E[] makeArray(int size, ModelsContext modelContext) {
		//noinspection unchecked
		return (E[]) Array.newInstance( enumType, size );
	}
}
