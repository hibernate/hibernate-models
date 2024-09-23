/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import org.hibernate.models.rendering.spi.Renderer;
import org.hibernate.models.rendering.spi.RenderingTarget;

/**
 * Descriptor for the annotation attribute types, acting as a producer for
 * {@link AttributeDescriptor}, {@link JdkValueConverter} and {@link JdkValueExtractor} references
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
			String name,
			Object attributeValue,
			RenderingTarget target,
			Renderer renderer,
			SourceModelContext modelContext) {
		target.addLine( "%s = %s", name, "..." );
	}

	void render(
			Object attributeValue,
			RenderingTarget target,
			Renderer renderer,
			SourceModelContext modelContext);
}
