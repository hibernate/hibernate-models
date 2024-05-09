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
public class StringValueExtractor extends AbstractValueExtractor<String> {
	public static final StringValueExtractor JANDEX_STRING_EXTRACTOR = new StringValueExtractor();

	@Override
	protected String extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return StringValueConverter.JANDEX_STRING_VALUE_WRAPPER.convert( jandexValue, buildingContext );
	}
}
