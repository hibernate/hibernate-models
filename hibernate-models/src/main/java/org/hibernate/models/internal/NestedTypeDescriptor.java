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

import org.hibernate.models.internal.jdk.JdkNestedValueConverter;
import org.hibernate.models.internal.jdk.JdkNestedValueExtractor;
import org.hibernate.models.spi.AnnotationDescriptor;
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

	private JdkNestedValueConverter<A> jdkConverter;
	private JdkNestedValueExtractor<A> jdkExtractor;

	public NestedTypeDescriptor(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	@Override
	public Class<A> getValueType() {
		return annotationType;
	}

	private AnnotationDescriptor<A> resolveDescriptor(SourceModelBuildingContext context) {
		if ( descriptor == null ) {
			descriptor = context.getAnnotationDescriptorRegistry().getDescriptor( annotationType );
		}
		return descriptor;
	}

	@Override
	public JdkValueConverter<A> createJdkValueConverter(SourceModelBuildingContext modelContext) {
		return resolveJdkValueConverter( modelContext );
	}

	public JdkNestedValueConverter<A> resolveJdkValueConverter(SourceModelBuildingContext modelContext) {
		if ( jdkConverter == null ) {
			jdkConverter = new JdkNestedValueConverter<>( resolveDescriptor( modelContext ) );
		}
		return jdkConverter;
	}

	@Override
	public JdkValueExtractor<A> createJdkValueExtractor(SourceModelBuildingContext modelContext) {
		return resolveJdkValueExtractor( modelContext );
	}

	public JdkValueExtractor<A> resolveJdkValueExtractor(SourceModelBuildingContext modelContext) {
		if ( jdkExtractor == null ) {
			jdkExtractor = new JdkNestedValueExtractor<>( resolveJdkValueConverter( modelContext ) );
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
