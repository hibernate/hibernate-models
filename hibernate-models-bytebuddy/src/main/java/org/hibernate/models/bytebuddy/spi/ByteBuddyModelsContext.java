/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.pool.TypePool;

/**
 * @author Steve Ebersole
 */
public interface ByteBuddyModelsContext extends SourceModelBuildingContext {
	TypePool getTypePool();

	<V> ValueConverter<V> getValueConverter(ValueTypeDescriptor<V> valueTypeDescriptor);
	<V> ValueExtractor<V> getValueExtractor(ValueTypeDescriptor<V> valueTypeDescriptor);
}
