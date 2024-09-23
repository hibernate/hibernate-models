/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractJdkValueExtractor<V> implements JdkValueExtractor<V> {
	@Override
	public <A extends Annotation> V extractValue(
			A usage,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext modelContext) {
		try {
			//noinspection unchecked
			final V rawValue = (V) attributeDescriptor.getAttributeMethod().invoke( usage );
			return wrap( rawValue, attributeDescriptor, modelContext );
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new AnnotationAccessException(
					String.format(
							Locale.ROOT,
							"Unable to extract attribute value [%s] from annotation [%s]",
							attributeDescriptor.getName(),
							usage
					),
					e
			);
		}
	}

	@Override
	public V extractValue(
			Annotation annotation,
			String attributeName,
			SourceModelBuildingContext buildingContext) {
		final AnnotationDescriptor<? extends Annotation> annDescriptor = buildingContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( annotation.annotationType() );
		return extractValue( annotation, annDescriptor.getAttribute( attributeName ), buildingContext );
	}

	protected abstract V wrap(
			V rawValue,
			AttributeDescriptor<V> attributeDescriptor,
			SourceModelBuildingContext buildingContext);
}
