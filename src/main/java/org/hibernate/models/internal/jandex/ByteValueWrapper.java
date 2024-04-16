/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a byte
 *
 * @author Steve Ebersole
 */
public class ByteValueWrapper implements ValueWrapper<Byte, AnnotationValue> {
	public static final ByteValueWrapper JANDEX_BYTE_VALUE_WRAPPER = new ByteValueWrapper();

	@Override
	public Byte wrap(AnnotationValue rawValue, SourceModelBuildingContext buildingContext) {
		assert rawValue != null;
		return rawValue.asByte();
	}
}
