/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting short values
 *
 * @author Steve Ebersole
 */
public class ShortValueExtractor extends AbstractValueExtractor<Short> {
	public static final ShortValueExtractor SHORT_EXTRACTOR = new ShortValueExtractor();

	protected Short extractAndWrap(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext buildingContext) {
		assert byteBuddyValue != null;
		return ShortValueConverter.SHORT_VALUE_WRAPPER.convert( byteBuddyValue, buildingContext );
	}

}
