/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.util.Locale;

import org.hibernate.models.internal.jandex.NestedValueConverter;
import org.hibernate.models.internal.jandex.NestedValueExtractor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for nested annotation values
 *
 * @author Steve Ebersole
 */
public class NestedTypeDescriptor<A extends Annotation> extends AbstractTypeDescriptor<A> {
	private final Class<A> annotationType;

	private AnnotationDescriptor<A> descriptor;

	private NestedValueConverter<A> jandexConverter;
	private NestedValueExtractor<A> jandexExtractor;

	private org.hibernate.models.internal.jdk.NestedValueConverter<A> jdkConverter;
	private org.hibernate.models.internal.jdk.NestedValueExtractor<A> jdkExtractor;

	public NestedTypeDescriptor(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public Class<A> getValueType() {
		return annotationType;
	}

	private AnnotationDescriptor<A> resolveDescriptor(SourceModelBuildingContext context) {
		if ( descriptor == null ) {
			descriptor = context
					.getAnnotationDescriptorRegistry()
					.resolveDescriptor( annotationType, (t) -> new StandardAnnotationDescriptor<>( annotationType, descriptor, context ) );
		}
		return descriptor;
	}

	@Override
	public JandexValueConverter<A> createJandexValueConverter(SourceModelBuildingContext buildingContext) {
		return resolveJandexWrapper( buildingContext );
	}

	public NestedValueConverter<A> resolveJandexWrapper(SourceModelBuildingContext buildingContext) {
		if ( jandexConverter == null ) {
			jandexConverter = new NestedValueConverter<>( resolveDescriptor( buildingContext ) );
		}
		return jandexConverter;
	}

	@Override
	public JandexValueExtractor<A> createJandexValueExtractor(SourceModelBuildingContext buildingContext) {
		return resolveJandexExtractor( buildingContext );
	}

	public NestedValueExtractor<A> resolveJandexExtractor(SourceModelBuildingContext buildingContext) {
		if ( jandexExtractor == null ) {
			this.jandexExtractor = new NestedValueExtractor<>( resolveJandexWrapper( buildingContext ) );
		}
		return jandexExtractor;
	}

	@Override
	public JdkValueConverter<A> createJdkValueConverter(SourceModelBuildingContext modelContext) {
		if ( jdkConverter == null ) {
			jdkConverter = new org.hibernate.models.internal.jdk.NestedValueConverter<>( descriptor );
		}
		return jdkConverter;
	}

	@Override
	public JdkValueExtractor<A> createJdkValueExtractor(SourceModelBuildingContext modelContext) {
		if ( jdkExtractor == null ) {
			jdkExtractor = new org.hibernate.models.internal.jdk.NestedValueExtractor<>( jdkConverter );
		}
		return jdkExtractor;
	}

	@Override
	public Object unwrap(A value) {
		return value;
	}

	@Override
	public void render(RenderingCollector collector, String name, Object attributeValue, SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		resolveDescriptor( modelContext ).renderUsage( collector, name, (A) attributeValue, modelContext );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		resolveDescriptor( modelContext ).renderUsage( collector, (A) attributeValue, modelContext );
	}

	@Override
	public A[] makeArray(int size, SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		return (A[]) Array.newInstance( resolveDescriptor( modelContext ).getAnnotationType(), size );
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
