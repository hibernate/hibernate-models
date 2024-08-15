/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for double values
 *
 * @author Steve Ebersole
 */
public class DoubleTypeDescriptor extends AbstractTypeDescriptor<Double> {
	public static final DoubleTypeDescriptor DOUBLE_TYPE_DESCRIPTOR = new DoubleTypeDescriptor();

	@Override
	public Class<Double> getValueType() {
		return Double.class;
	}

	@Override
	public Object unwrap(Double value) {
		return value;
	}

	@Override
	public Double[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Double[size];
	}
}
