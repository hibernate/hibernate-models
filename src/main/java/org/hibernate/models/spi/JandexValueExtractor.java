/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import org.jboss.jandex.AnnotationInstance;

/**
 * Support for extracting attribute values from Jandex
 * {@linkplain org.jboss.jandex.AnnotationInstance} references.
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
