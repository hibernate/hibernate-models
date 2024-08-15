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
 * Extracts boolean values from an attribute
 *
 * @author Steve Ebersole
 */
public class BooleanValueExtractor extends AbstractValueExtractor<Boolean> {
	public static final BooleanValueExtractor JANDEX_BOOLEAN_EXTRACTOR = new BooleanValueExtractor();

	@Override
	protected Boolean extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return BooleanValueConverter.JANDEX_BOOLEAN_VALUE_WRAPPER.convert( jandexValue, buildingContext );
	}
}
