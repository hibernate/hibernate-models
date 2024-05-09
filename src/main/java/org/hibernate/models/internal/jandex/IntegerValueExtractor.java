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
public class IntegerValueExtractor extends AbstractValueExtractor<Integer> {
	public static final IntegerValueExtractor JANDEX_INTEGER_EXTRACTOR = new IntegerValueExtractor();

	@Override
	protected Integer extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return IntegerValueConverter.JANDEX_INTEGER_VALUE_WRAPPER.convert( jandexValue, buildingContext );
	}
}
