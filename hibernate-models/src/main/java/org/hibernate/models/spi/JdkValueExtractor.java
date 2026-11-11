/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Steve Ebersole
 */
public interface JdkValueExtractor<V> {
	/**
	 * Extract the value of the named attribute from the given annotation
	 */
	@Nullable
	<A extends Annotation> V extractValue(@Nonnull A usage, @Nonnull String attributeName, @Nonnull ModelsContext modelContext);

	/**
	 * Extract the value of the described attribute from the given annotation
	 */
	@Nullable
	<A extends Annotation> V extractValue(
			@Nonnull A usage,
			@Nonnull AttributeDescriptor<V> attributeDescriptor,
			@Nonnull ModelsContext modelContext);
}
