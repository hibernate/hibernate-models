/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for string values
 *
 * @author Steve Ebersole
 */
public class StringTypeDescriptor extends AbstractTypeDescriptor<String> {
	public static final StringTypeDescriptor STRING_TYPE_DESCRIPTOR = new StringTypeDescriptor();

	@Override
	public Class<String> getValueType() {
		return String.class;
	}

	@Override
	public Object unwrap(String value) {
		return value;
	}

	@Override
	public String[] makeArray(int size, ModelsContext modelContext) {
		return new String[size];
	}
}
