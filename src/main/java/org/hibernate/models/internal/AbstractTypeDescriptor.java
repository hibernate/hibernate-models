/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.Locale;

import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ValueTypeDescriptor;

/**
 * Base support for {@linkplain AttributeDescriptor} implementations
 *
 * @author Steve Ebersole
 */
public abstract class AbstractTypeDescriptor<V> implements ValueTypeDescriptor<V> {
	@Override
	public AttributeDescriptor<V> createAttributeDescriptor(
			AnnotationDescriptor<?> annotationDescriptor,
			String attributeName) {
		return new AttributeDescriptorImpl<>( annotationDescriptor.getAnnotationType(), attributeName, this );
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"AttributeTypeDescriptor(%s)",
				getWrappedValueType().getName()
		);
	}
}
