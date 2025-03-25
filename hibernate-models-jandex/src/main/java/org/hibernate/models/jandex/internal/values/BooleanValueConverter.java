/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a boolean
 *
 * @author Steve Ebersole
 */
public class BooleanValueConverter implements JandexValueConverter<Boolean> {
	public static final BooleanValueConverter JANDEX_BOOLEAN_VALUE_WRAPPER = new BooleanValueConverter();

	@Override
	public Boolean convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext) {
		assert jandexValue != null;
		return jandexValue.asBoolean();
	}
}
