/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for boolean values
 *
 * @author Steve Ebersole
 */
public class BooleanTypeDescriptor extends AbstractTypeDescriptor<Boolean> {
	public static final BooleanTypeDescriptor BOOLEAN_TYPE_DESCRIPTOR = new BooleanTypeDescriptor();

	@Override
	public Class<Boolean> getValueType() {
		return Boolean.class;
	}

	@Override
	public Object unwrap(Boolean value) {
		return value;
	}

	@Override
	public Boolean[] makeArray(int size, ModelsContext modelContext) {
		return new Boolean[size];
	}
}
