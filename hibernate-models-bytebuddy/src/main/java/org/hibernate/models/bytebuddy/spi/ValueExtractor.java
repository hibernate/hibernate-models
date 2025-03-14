/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import net.bytebuddy.description.annotation.AnnotationDescription;

/**
 * Contract to extract a named attribute value from an {@linkplain AnnotationDescription annotation}.
 *
 * @author Steve Ebersole
 */
public interface ValueExtractor<V> {
	/**
	 * Extract the value of the named attribute from the given annotation
	 */
	V extractValue(AnnotationDescription annotation, String attributeName, SourceModelBuildingContext modelContext);

	/**
	 * Extract the value of the described attribute from the given annotation
	 */
	default V extractValue(
			AnnotationDescription annotation,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext modelContext) {
		return extractValue( annotation, attributeDescriptor.getName(), modelContext );
	}
}
