/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import org.jboss.jandex.IndexView;

/**
 * SourceModelBuildingContext implementation using
 * <a href="https://github.com/smallrye/jandex">Jandex</a>.
 *
 * @author Steve Ebersole
 */
public interface JandexModelContext extends SourceModelBuildingContext {
	/**
	 * The Jandex index
	 */
	IndexView getJandexIndex();

	/**
	 * Get a {@linkplain JandexValueConverter value converter}
	 * capable of converting a Jandex {@linkplain org.jboss.jandex.AnnotationValue}
	 * to the type specified by {@code valueTypeDescriptor}.
	 */
	<V> JandexValueConverter<V> getJandexValueConverter(ValueTypeDescriptor<V> valueTypeDescriptor);

	/**
	 * Get a {@linkplain JandexValueExtractor value extractor}
	 * capable of extracting an annotation attribute from a
	 * Jandex {@linkplain org.jboss.jandex.AnnotationInstance}
	 * and returning it's {@linkplain #getJandexValueConverter converted value}.
	 */
	<V> JandexValueExtractor<V> getJandexValueExtractor(ValueTypeDescriptor<V> valueTypeDescriptor);
}
