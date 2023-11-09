/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * Descriptor for the annotation attribute types, acting as a producer for
 * {@link AttributeDescriptor}, {@link ValueWrapper} and {@link ValueExtractor} references
 *
 * @author Steve Ebersole
 */
public interface ValueTypeDescriptor<V> {
	/**
	 * The type for the value as modeled in {@linkplain AnnotationUsage}.
	 */
	Class<V> getWrappedValueType();

	/**
	 * Factory for creating typed {@linkplain AttributeDescriptor} references
	 */
	AttributeDescriptor<V> createAttributeDescriptor(AnnotationDescriptor<?> annotationDescriptor, String attributeName);

	ValueWrapper<V, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext);

	ValueExtractor<AnnotationInstance,V> createJandexExtractor(SourceModelBuildingContext buildingContext);

	ValueWrapper<V,?> createJdkWrapper(SourceModelBuildingContext buildingContext);

	ValueExtractor<Annotation,V> createJdkExtractor(SourceModelBuildingContext buildingContext);
}
