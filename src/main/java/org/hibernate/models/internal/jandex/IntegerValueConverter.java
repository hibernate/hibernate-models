/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueConverter;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a float
 *
 * @author Steve Ebersole
 */
public class IntegerValueConverter implements JandexValueConverter<Integer> {
	public static final IntegerValueConverter JANDEX_INTEGER_VALUE_WRAPPER = new IntegerValueConverter();

	@Override
	public Integer convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asInt();
	}
}
