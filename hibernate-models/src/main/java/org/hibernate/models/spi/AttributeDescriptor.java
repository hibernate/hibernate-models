/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Method;

/**
 * Descriptor for an annotation attribute
 *
 * @author Steve Ebersole
 */
public interface AttributeDescriptor<T> {
	/**
	 * The name of the attribute.
	 */
	String getName();

	/**
	 * Descriptor for this attribute's type
	 */
	ValueTypeDescriptor<T> getTypeDescriptor();

	/**
	 * The attribute's type.
	 */
	default Class<T> getValueType() {
		return getTypeDescriptor().getValueType();
	}

	/**
	 * The attribute's defined default value, if one.
	 */
	T getDefaultValue();

	/**
	 * The attribute method.
	 */
	Method getAttributeMethod();
}
