/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for long values
 *
 * @author Steve Ebersole
 */
public class LongTypeDescriptor extends AbstractTypeDescriptor<Long> {
	public static final LongTypeDescriptor LONG_TYPE_DESCRIPTOR = new LongTypeDescriptor();

	@Override
	public Class<Long> getValueType() {
		return Long.class;
	}

	@Override
	public Object unwrap(Long value) {
		return value;
	}

	@Override
	public Long[] makeArray(int size, ModelsContext modelContext) {
		return new Long[size];
	}
}
