/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a double
 *
 * @author Steve Ebersole
 */
public class DoubleValueWrapper implements ValueWrapper<Double,AnnotationValue> {
	public static final DoubleValueWrapper JANDEX_DOUBLE_VALUE_WRAPPER = new DoubleValueWrapper();

	@Override
	public Double wrap(
			AnnotationValue rawValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		assert rawValue != null;
		return rawValue.asDouble();
	}
}
