/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.Locale;

import org.hibernate.models.internal.jandex.NestedValueExtractor;
import org.hibernate.models.internal.jandex.NestedValueWrapper;
import org.hibernate.models.internal.jdk.AnnotationDescriptorImpl;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * Descriptor for nested annotation values
 *
 * @author Steve Ebersole
 */
public class NestedTypeDescriptor<A extends Annotation> extends AbstractTypeDescriptor<AnnotationUsage<A>> {
	private final Class<A> annotationType;

	private AnnotationDescriptor<A> descriptor;

	private NestedValueWrapper<A> jandexWrapper;
	private NestedValueExtractor<A> jandexExtractor;

	private org.hibernate.models.internal.jdk.NestedValueWrapper<A> jdkWrapper;
	private org.hibernate.models.internal.jdk.NestedValueExtractor<A> jdkExtractor;

	public NestedTypeDescriptor(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public Class<AnnotationUsage<A>> getWrappedValueType() {
		//noinspection unchecked,rawtypes
		return (Class) AnnotationUsage.class;
	}

	private AnnotationDescriptor<A> resolveDescriptor(SourceModelBuildingContext context) {
		if ( descriptor == null ) {
			descriptor = context
					.getAnnotationDescriptorRegistry()
					.resolveDescriptor( annotationType, (t) -> new AnnotationDescriptorImpl<>( annotationType ) );
		}
		return descriptor;
	}

	@Override
	public ValueWrapper<AnnotationUsage<A>, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return resolveJandexWrapper( buildingContext );
	}

	public NestedValueWrapper<A> resolveJandexWrapper(SourceModelBuildingContext buildingContext) {
		if ( jandexWrapper == null ) {
			jandexWrapper = new NestedValueWrapper<>( resolveDescriptor( buildingContext ) );
		}
		return jandexWrapper;
	}

	@Override
	public ValueExtractor<AnnotationInstance, AnnotationUsage<A>> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return resolveJandexExtractor( buildingContext );
	}

	public NestedValueExtractor<A> resolveJandexExtractor(SourceModelBuildingContext buildingContext) {
		if ( jandexExtractor == null ) {
			this.jandexExtractor = new NestedValueExtractor<>( resolveJandexWrapper( buildingContext ) );
		}
		return jandexExtractor;
	}

	@Override
	public ValueWrapper<AnnotationUsage<A>, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		return resolveJdkWrapper( buildingContext );
	}

	public org.hibernate.models.internal.jdk.NestedValueWrapper<A> resolveJdkWrapper(SourceModelBuildingContext buildingContext) {
		if ( jdkWrapper == null ) {
			jdkWrapper = new org.hibernate.models.internal.jdk.NestedValueWrapper<>( resolveDescriptor( buildingContext ) );
		}
		return jdkWrapper;
	}

	@Override
	public ValueExtractor<Annotation, AnnotationUsage<A>> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		return resolveJdkExtractor( buildingContext );
	}

	public ValueExtractor<Annotation, AnnotationUsage<A>> resolveJdkExtractor(SourceModelBuildingContext buildingContext) {
		if ( jdkExtractor == null ) {
			jdkExtractor = new org.hibernate.models.internal.jdk.NestedValueExtractor<>( resolveJdkWrapper( buildingContext ) );
		}
		return jdkExtractor;
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"AttributeTypeDescriptor(%s)",
				descriptor.getAnnotationType().getName()
		);
	}
}
