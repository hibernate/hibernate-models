/*
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
import org.hibernate.models.rendering.spi.Renderer;
import org.hibernate.models.rendering.spi.RenderingTarget;
import org.hibernate.models.spi.ModelsContext;

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

	private AnnotationDescriptor<A> resolveDescriptor(ModelsContext context) {
		if ( descriptor == null ) {
			descriptor = context.getAnnotationDescriptorRegistry().getDescriptor( annotationType );
		}
		return descriptor;
	}

	@Override
	public JdkValueConverter<A> createJdkValueConverter(ModelsContext modelContext) {
		return resolveJdkValueConverter( modelContext );
	}

	public JdkNestedValueConverter<A> resolveJdkValueConverter(ModelsContext modelContext) {
		if ( jdkConverter == null ) {
			jdkConverter = new JdkNestedValueConverter<>( resolveDescriptor( modelContext ) );
		}
		return jdkConverter;
	}

	@Override
	public JdkValueExtractor<A> createJdkValueExtractor(ModelsContext modelContext) {
		return resolveJdkValueExtractor( modelContext );
	}

	public JdkValueExtractor<A> resolveJdkValueExtractor(ModelsContext modelContext) {
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
	public void render(
			String name,
			Object attributeValue,
			RenderingTarget target,
			Renderer renderer,
			ModelsContext modelContext) {
		//noinspection unchecked
		renderer.renderNestedAnnotation( name, (A) attributeValue, modelContext );
	}

	@Override
	public void render(Object attributeValue, RenderingTarget target, Renderer renderer, ModelsContext modelContext) {
		//noinspection unchecked
		renderer.renderNestedAnnotation( (A) attributeValue, modelContext );
	}

	@Override
	public A[] makeArray(int size, ModelsContext modelContext) {
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
