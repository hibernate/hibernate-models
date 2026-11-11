/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Steve Ebersole
 */
public interface JdkValueConverter<V> {
	/**
	 * Convert an annotation attribute value. Scalar pass-through converters accept
	 * and return {@code null} for incomplete usages; composite converters require
	 * a nonnull input.
	 */
	@Nullable
	V convert(@Nullable V rawValue, @Nonnull ModelsContext modelContext);
}
