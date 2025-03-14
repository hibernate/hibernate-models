/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting Class values
 *
 * @author Steve Ebersole
 */
public class ClassValueExtractor extends AbstractValueExtractor<Class<?>> {
	public static final ClassValueExtractor CLASS_EXTRACTOR = new ClassValueExtractor();

	@Override
	protected Class<?> extractAndWrap(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext buildingContext) {
		assert byteBuddyValue != null;
		return ClassValueConverter.CLASS_VALUE_WRAPPER.convert( byteBuddyValue, buildingContext );
	}
}
