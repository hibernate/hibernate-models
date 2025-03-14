/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting string values
 *
 * @author Steve Ebersole
 */
public class StringValueExtractor extends AbstractValueExtractor<String> {
	public static final StringValueExtractor STRING_EXTRACTOR = new StringValueExtractor();

	@Override
	protected String extractAndWrap(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext buildingContext) {
		return StringValueConverter.STRING_VALUE_WRAPPER.convert( byteBuddyValue, buildingContext );
	}
}
