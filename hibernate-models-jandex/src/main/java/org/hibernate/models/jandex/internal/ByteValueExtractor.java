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
public class ByteValueExtractor extends AbstractValueExtractor<Byte> {
	public static final ByteValueExtractor JANDEX_BYTE_EXTRACTOR = new ByteValueExtractor();

	@Override
	protected Byte extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return ByteValueConverter.JANDEX_BYTE_VALUE_WRAPPER.convert( jandexValue, buildingContext );
	}
}
