/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Method;

import jakarta.annotation.Nonnull;

/**
 * Descriptor for an annotation attribute
 *
 * @author Steve Ebersole
 */
public interface AttributeDescriptor<T> {
	/**
	 * The name of the attribute.
	 */
	@Nonnull
	String getName();

	/**
	 * Descriptor for this attribute's type
	 */
	@Nonnull
	ValueTypeDescriptor<T> getTypeDescriptor();

	/**
	 * The attribute method.
	 */
	@Nonnull
	Method getAttributeMethod();

	default boolean isMultiValued() {
		return getAttributeMethod().getReturnType().isArray();
	}
}
