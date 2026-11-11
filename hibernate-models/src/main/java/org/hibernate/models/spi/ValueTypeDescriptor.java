/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import org.hibernate.models.rendering.spi.Renderer;
import org.hibernate.models.rendering.spi.RenderingTarget;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

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
	@Nonnull
	Class<V> getValueType();

	/**
	 * Factory for creating typed {@linkplain AttributeDescriptor} references
	 */
	@Nonnull
	AttributeDescriptor<V> createAttributeDescriptor(@Nonnull Class<? extends Annotation> annotationType, @Nonnull String attributeName);

	@Nonnull
	JdkValueConverter<V> createJdkValueConverter(@Nonnull ModelsContext modelContext);

	@Nonnull
	JdkValueExtractor<V> createJdkValueExtractor(@Nonnull ModelsContext modelContext);

	/**
	 * Unwrap a modeled annotation attribute value. Scalar descriptors pass
	 * {@code null} through for incomplete usages; array descriptors require
	 * a nonnull array.
	 */
	@Nullable
	Object unwrap(@Nullable V value);

	@Nonnull
	V[] makeArray(int size, @Nonnull ModelsContext modelContext);

	/**
	 * Render a named attribute value. Scalar descriptors accept {@code null}
	 * for incomplete usages; array and nested annotation descriptors require
	 * a nonnull value.
	 */
	default void render(
			@Nonnull String name,
			@Nullable Object attributeValue,
			@Nonnull RenderingTarget target,
			@Nonnull Renderer renderer,
			@Nonnull ModelsContext modelContext) {
		target.addLine( "%s = %s", name, "..." );
	}

	/**
	 * Render an unnamed attribute value, with the same null handling as
	 * {@link #render(String, Object, RenderingTarget, Renderer, ModelsContext)}.
	 */
	void render(
			@Nullable Object attributeValue,
			@Nonnull RenderingTarget target,
			@Nonnull Renderer renderer,
			@Nonnull ModelsContext modelContext);
}
