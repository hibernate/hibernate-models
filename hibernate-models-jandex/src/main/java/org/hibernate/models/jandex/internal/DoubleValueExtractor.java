/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class DoubleValueExtractor extends AbstractValueExtractor<Double> {
	public static final DoubleValueExtractor JANDEX_DOUBLE_EXTRACTOR = new DoubleValueExtractor();

	@Override
	protected Double extractAndWrap(
			AnnotationValue jandexValue,
			SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return DoubleValueConverter.JANDEX_DOUBLE_VALUE_WRAPPER.convert( jandexValue, buildingContext );
	}
}
