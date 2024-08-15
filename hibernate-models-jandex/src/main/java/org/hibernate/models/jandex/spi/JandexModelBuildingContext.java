/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.jandex.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import org.jboss.jandex.IndexView;

/**
 * @author Steve Ebersole
 */
public interface JandexModelBuildingContext extends SourceModelBuildingContext {
	IndexView getJandexIndex();

	<V> JandexValueConverter<V> getJandexValueConverter(ValueTypeDescriptor<V> valueTypeDescriptor);
	<V> JandexValueExtractor<V> getJandexValueExtractor(ValueTypeDescriptor<V> valueTypeDescriptor);
}
