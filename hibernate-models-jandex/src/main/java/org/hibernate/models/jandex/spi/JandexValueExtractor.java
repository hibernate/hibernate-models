/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import jakarta.annotation.Nonnull;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ModelsContext;

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
	@Nonnull
	V extractValue(@Nonnull AnnotationInstance annotation, @Nonnull String attributeName, @Nonnull ModelsContext modelContext);

	/**
	 * Extract the value of the described attribute from the given annotation
	 */
	@Nonnull
	default V extractValue(
			@Nonnull AnnotationInstance annotation,
			@Nonnull AttributeDescriptor<V> attributeDescriptor,
			@Nonnull ModelsContext modelContext) {
		return extractValue( annotation, attributeDescriptor.getName(), modelContext );
	}
}
