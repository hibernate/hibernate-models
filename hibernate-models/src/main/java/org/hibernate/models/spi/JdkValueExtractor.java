/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

/**
 * @author Steve Ebersole
 */
public interface JdkValueExtractor<V> {
	/**
	 * Extract the value of the named attribute from the given annotation
	 */
	<A extends Annotation> V extractValue(A usage, String attributeName, SourceModelBuildingContext modelContext);

	/**
	 * Extract the value of the described attribute from the given annotation
	 */
	<A extends Annotation> V extractValue(
			A usage,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext modelContext);
}
