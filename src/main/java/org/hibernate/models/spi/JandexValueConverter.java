/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import org.jboss.jandex.AnnotationValue;

/**
 * Used in processing Jandex references.
 * Given a Jandex {@linkplain AnnotationValue}, converts to the corresponding "value type".
 *
 * @author Steve Ebersole
 */
public interface JandexValueConverter<V> {
	V convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext);
}
