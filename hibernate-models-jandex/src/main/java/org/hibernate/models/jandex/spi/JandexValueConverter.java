/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.spi;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Used in processing Jandex references. Given a Jandex
 * {@linkplain AnnotationValue}, converts to the corresponding
 * {@linkplain org.hibernate.models.spi.ValueTypeDescriptor value type}.
 *
 * @author Steve Ebersole
 */
public interface JandexValueConverter<V> {
	/**
	 * Perform the conversion.
	 */
	V convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext);
}
