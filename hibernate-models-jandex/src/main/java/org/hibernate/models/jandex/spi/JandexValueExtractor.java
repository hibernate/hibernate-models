/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationInstance;

/**
 * Support for extracting attribute values from Jandex
 * {@linkplain org.jboss.jandex.AnnotationInstance} references.
 *
 * @implSpec Coordinates with {@linkplain JandexValueConverter} for
 * handling the raw extracted value.
 *
 * @param <V> The value type
 *
 * @author Steve Ebersole
 */
public interface JandexValueExtractor<V> {
	/**
	 * Extract the value of the named attribute from the given annotation
	 */
	V extractValue(AnnotationInstance annotation, String attributeName, SourceModelBuildingContext modelContext);

	/**
	 * Extract the value of the described attribute from the given annotation
	 */
	default V extractValue(
			AnnotationInstance annotation,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext modelContext) {
		return extractValue( annotation, attributeDescriptor.getName(), modelContext );
	}
}
