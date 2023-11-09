/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

/**
 * Common ValueWrapper support for implementations with the same raw and
 * wrapped form (Byte, Boolean, etc.) as opposed to ClassDetails, AnnotationUsage, etc.
 *
 * @author Steve Ebersole
 */
public class PassThruWrapper<V> implements ValueWrapper<V,V> {
	public static final PassThruWrapper PASS_THRU_WRAPPER = new PassThruWrapper();

	@Override
	public V wrap(V rawValue, AnnotationTarget target, SourceModelBuildingContext buildingContext) {
		return rawValue;
	}
}
