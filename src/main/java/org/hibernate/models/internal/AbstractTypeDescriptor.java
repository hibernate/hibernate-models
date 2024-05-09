/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.Locale;

import org.hibernate.models.internal.jdk.PassThruConverter;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

/**
 * Base support for {@linkplain AttributeDescriptor} implementations
 *
 * @author Steve Ebersole
 */
public abstract class AbstractTypeDescriptor<V> implements ValueTypeDescriptor<V> {
	@Override
	public AttributeDescriptor<V> createAttributeDescriptor(
			Class<? extends Annotation> annotationType,
			String attributeName) {
		return new AttributeDescriptorImpl<>( annotationType, attributeName, this );
	}

	@Override
	public JdkValueConverter<V> createJdkValueConverter(SourceModelBuildingContext modelContext) {
		return PassThruConverter.passThruConverter();
	}

	@Override
	public JdkValueExtractor<V> createJdkValueExtractor(SourceModelBuildingContext modelContext) {
		return PassThruExtractor.passThruExtractor();
	}

	@Override
	public void render(RenderingCollector collector, String name, Object attributeValue, SourceModelBuildingContext modelContext) {
		collector.addLine( "%s = %s", name, attributeValue );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext) {
		collector.addLine( "%s", attributeValue );
	}

	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"AttributeTypeDescriptor(%s)",
				getValueType().getName()
		);
	}
}
