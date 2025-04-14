/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for byte values
 *
 * @author Steve Ebersole
 */
public class ByteTypeDescriptor extends AbstractTypeDescriptor<Byte> {
	public static final ByteTypeDescriptor BYTE_TYPE_DESCRIPTOR = new ByteTypeDescriptor();

	@Override
	public Class<Byte> getValueType() {
		return Byte.class;
	}

	@Override
	public Object unwrap(Byte value) {
		return value;
	}

	@Override
	public Byte[] makeArray(int size, ModelsContext modelContext) {
		return new Byte[size];
	}
}
