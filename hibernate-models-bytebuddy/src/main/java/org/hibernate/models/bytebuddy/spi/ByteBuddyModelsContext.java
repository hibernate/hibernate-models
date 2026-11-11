/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import jakarta.annotation.Nonnull;

import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.pool.TypePool;

/**
 * ModelsContext implementation using <a href="https://bytebuddy.net/">Byte Buddy</a>.
 *
 * @author Steve Ebersole
 */
public interface ByteBuddyModelsContext extends ModelsContext {
	@Nonnull
	TypePool getTypePool();

	@Nonnull
	<V> ValueExtractor<V> getValueExtractor(@Nonnull ValueTypeDescriptor<V> valueTypeDescriptor);
}
