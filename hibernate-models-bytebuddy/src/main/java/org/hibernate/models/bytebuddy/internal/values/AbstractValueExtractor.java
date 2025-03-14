/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationValue;


/**
 * Support for ByteBuddy-based extractors
 *
 * @author Steve Ebersole
 */
public abstract class AbstractValueExtractor<W> implements ValueExtractor<W> {

	protected abstract W extractAndWrap(AnnotationValue<?,?> byteBuddyValue, SourceModelBuildingContext buildingContext);

	@Override
	public W extractValue(
			AnnotationDescription annotation,
			String attributeName,
			SourceModelBuildingContext buildingContext) {
		final AnnotationValue<?, ?> value = annotation.getValue( attributeName );
		return extractAndWrap( value, buildingContext );
	}
}
