/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for short values
 *
 * @author Steve Ebersole
 */
public class ShortTypeDescriptor extends AbstractTypeDescriptor<Short> {
	public static final ShortTypeDescriptor SHORT_TYPE_DESCRIPTOR = new ShortTypeDescriptor();

	@Override
	public Class<Short> getValueType() {
		return Short.class;
	}

	@Override
	public Object unwrap(Short value) {
		return value;
	}

	@Override
	public Short[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Short[size];
	}
}
