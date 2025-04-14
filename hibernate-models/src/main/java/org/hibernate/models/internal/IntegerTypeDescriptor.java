/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for integer values
 *
 * @author Steve Ebersole
 */
public class IntegerTypeDescriptor extends AbstractTypeDescriptor<Integer> {
	public static final IntegerTypeDescriptor INTEGER_TYPE_DESCRIPTOR = new IntegerTypeDescriptor();

	@Override
	public Class<Integer> getValueType() {
		return Integer.class;
	}

	@Override
	public Object unwrap(Integer value) {
		return value;
	}

	@Override
	public Integer[] makeArray(int size, ModelsContext modelContext) {
		return new Integer[size];
	}
}
