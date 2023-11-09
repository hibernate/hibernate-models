/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;

/**
 * @author Steve Ebersole
 */
public abstract class AbstractValueExtractor<W,R> implements ValueExtractor<Annotation, W> {

	protected abstract W wrap(
			R rawValue,
			AttributeDescriptor<W> attributeDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext);

	@Override
	public W extractValue(
			Annotation annotation,
			String attributeName,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		final AnnotationDescriptor<? extends Annotation> annDescriptor = buildingContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( annotation.annotationType() );
		return extractValue( annotation, annDescriptor.getAttribute( attributeName ), target, buildingContext );
	}

	@Override
	public W extractValue(
			Annotation annotation,
			AttributeDescriptor<W> attributeDescriptor,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		try {
			//noinspection unchecked
			final R rawValue = (R) attributeDescriptor.getAttributeMethod().invoke( annotation );
			return wrap( rawValue, attributeDescriptor, target, buildingContext );
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new AnnotationAccessException(
					String.format(
							Locale.ROOT,
							"Unable to extract attribute value [%s] from annotation [%s]",
							attributeDescriptor.getName(),
							annotation
					),
					e
			);
		}
	}
}
