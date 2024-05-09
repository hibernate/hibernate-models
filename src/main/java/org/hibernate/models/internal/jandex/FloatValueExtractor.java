/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class FloatValueExtractor extends AbstractValueExtractor<Float> {
	public static final FloatValueExtractor JANDEX_FLOAT_EXTRACTOR = new FloatValueExtractor();

	@Override
	protected Float extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return FloatValueConverter.JANDEX_FLOAT_VALUE_WRAPPER.convert( jandexValue, buildingContext );
	}
}
