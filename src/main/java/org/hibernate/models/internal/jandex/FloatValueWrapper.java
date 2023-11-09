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
 * Wraps AnnotationValue as a float
 *
 * @author Steve Ebersole
 */
public class FloatValueWrapper implements ValueWrapper<Float,AnnotationValue> {
	public static final FloatValueWrapper JANDEX_FLOAT_VALUE_WRAPPER = new FloatValueWrapper();

	@Override
	public Float wrap(
			AnnotationValue rawValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		assert rawValue != null;
		return rawValue.asFloat();
	}
}
