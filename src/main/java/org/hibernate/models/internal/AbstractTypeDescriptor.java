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
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;
import org.hibernate.models.spi.ValueWrapper;

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
	public V createValue(
			AttributeDescriptor<?> attributeDescriptor,
			SourceModelBuildingContext context) {
		final Object defaultValue = attributeDescriptor.getAttributeMethod().getDefaultValue();
		if ( defaultValue == null ) {
			// a non-defaulted attribute, just return null for the baseline
			return null;
		}

		//noinspection unchecked
		final ValueWrapper<V, Object> valueWrapper = (ValueWrapper<V, Object>) createJdkWrapper( context );
		return valueWrapper.wrap( defaultValue, context );
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
