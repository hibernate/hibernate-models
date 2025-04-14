/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.ModelsContext;

/**
 * @author Steve Ebersole
 */
public class JdkArrayValueExtractor<V> extends AbstractJdkValueExtractor<V[]> {
	private final JdkValueConverter<V[]> converter;

	public JdkArrayValueExtractor(JdkValueConverter<V[]> converter) {
		this.converter = converter;
	}

	@Override
	public <A extends Annotation> V[] extractValue(
			A usage,
			AttributeDescriptor<V[]> attributeDescriptor,
			ModelsContext modelContext) {
		return super.extractValue( usage, attributeDescriptor, modelContext );
	}

	@Override
	public V[] extractValue(Annotation annotation, String attributeName, ModelsContext modelsContext) {
		return super.extractValue( annotation, attributeName, modelsContext );
	}

	@Override
	protected V[] wrap(
			V[] rawValue,
			AttributeDescriptor<V[]> attributeDescriptor,
			ModelsContext modelsContext) {
		return converter.convert( rawValue, modelsContext );
	}
}
