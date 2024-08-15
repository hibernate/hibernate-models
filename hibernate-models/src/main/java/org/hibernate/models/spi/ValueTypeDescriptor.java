/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

/**
 * Descriptor for the annotation attribute types, acting as a producer for
 * {@link AttributeDescriptor}, {@link JandexValueConverter} and {@link JandexValueExtractor} references
 *
 * @author Steve Ebersole
 */
public interface ValueTypeDescriptor<V> {
	/**
	 * The type for the value.
	 */
	Class<V> getValueType();

	/**
	 * Factory for creating typed {@linkplain AttributeDescriptor} references
	 */
	AttributeDescriptor<V> createAttributeDescriptor(Class<? extends Annotation> annotationType, String attributeName);

	JdkValueConverter<V> createJdkValueConverter(SourceModelBuildingContext modelContext);

	JdkValueExtractor<V> createJdkValueExtractor(SourceModelBuildingContext modelContext);

	Object unwrap(V value);

	V[] makeArray(int size, SourceModelBuildingContext modelContext);

	default void render(
			RenderingCollector collector,
			String name,
			Object attributeValue,
			SourceModelBuildingContext modelContext) {
		collector.addLine( "%s=%s", name, "..." );
	}

	void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext);
}
