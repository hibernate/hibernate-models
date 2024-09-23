/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * @author Steve Ebersole
 */
public interface JdkValueConverter<V> {
	V convert(V rawValue, SourceModelBuildingContext modelContext);
}
