/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class PassThruExtractor<V> extends AbstractValueExtractor<V> {
	@SuppressWarnings("rawtypes")
	public static final PassThruExtractor PASS_THRU_EXTRACTOR = new PassThruExtractor();

	public static <V> JdkValueExtractor<V> passThruExtractor() {
		//noinspection unchecked
		return PASS_THRU_EXTRACTOR;
	}

	@Override
	protected V wrap(
			V rawValue,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext buildingContext) {
		return rawValue;
	}
}
