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
 * Wraps AnnotationValue as a float
 *
 * @author Steve Ebersole
 */
public class ShortValueWrapper implements ValueWrapper<Short,AnnotationValue> {
	public static final ShortValueWrapper JANDEX_SHORT_VALUE_WRAPPER = new ShortValueWrapper();

	@Override
	public Short wrap(AnnotationValue rawValue, SourceModelBuildingContext buildingContext) {
		assert rawValue != null;
		return rawValue.asShort();
	}
}
