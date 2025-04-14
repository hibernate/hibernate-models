/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Used in processing ByteBuddy references.
 * Given a ByteBuddy {@linkplain AnnotationValue}, converts to the corresponding "value type".
 *
 * @param <V> The value type.
 *
 * @author Steve Ebersole
 */
public interface ValueConverter<V> {
	V convert(AnnotationValue<?,?> attributeValue, ModelsContext modelContext);
}
