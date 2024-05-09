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
public class LongValueExtractor extends AbstractValueExtractor<Long> {
	public static final LongValueExtractor JANDEX_LONG_EXTRACTOR = new LongValueExtractor();

	@Override
	protected Long extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return LongValueConverter.JANDEX_LONG_VALUE_WRAPPER.convert( jandexValue, buildingContext );
	}
}
