/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Common ValueWrapper support for implementations with the same raw and
 * wrapped form (Byte, Boolean, etc.) as opposed to ClassDetails, AnnotationUsage, etc.
 *
 * @author Steve Ebersole
 */
public class JdkPassThruConverter<V> implements JdkValueConverter<V> {
	@SuppressWarnings("rawtypes")
	public static final JdkPassThruConverter PASS_THRU_CONVERTER = new JdkPassThruConverter();

	public static <V> JdkPassThruConverter<V> passThruConverter() {
		//noinspection unchecked
		return PASS_THRU_CONVERTER;
	}

	@Override
	public V convert(V rawValue, SourceModelBuildingContext modelContext) {
		return rawValue;
	}
}
