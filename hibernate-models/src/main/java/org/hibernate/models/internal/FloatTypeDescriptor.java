/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for float values
 *
 * @author Steve Ebersole
 */
public class FloatTypeDescriptor extends AbstractTypeDescriptor<Float> {
	public static final FloatTypeDescriptor FLOAT_TYPE_DESCRIPTOR = new FloatTypeDescriptor();

	@Override
	public Class<Float> getValueType() {
		return Float.class;
	}

	@Override
	public Object unwrap(Float value) {
		return value;
	}

	@Override
	public Float[] makeArray(int size, ModelsContext modelContext) {
		return new Float[size];
	}
}
