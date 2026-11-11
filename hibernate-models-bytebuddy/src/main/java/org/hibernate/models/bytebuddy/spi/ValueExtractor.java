/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import jakarta.annotation.Nonnull;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ModelsContext;

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
	@Nonnull
	V extractValue(@Nonnull AnnotationDescription annotation, @Nonnull String attributeName, @Nonnull ModelsContext modelContext);

	/**
	 * Extract the value of the described attribute from the given annotation
	 */
	@Nonnull
	default V extractValue(
			@Nonnull AnnotationDescription annotation,
			@Nonnull AttributeDescriptor<V> attributeDescriptor,
			@Nonnull ModelsContext modelContext) {
		return extractValue( annotation, attributeDescriptor.getName(), modelContext );
	}
}
