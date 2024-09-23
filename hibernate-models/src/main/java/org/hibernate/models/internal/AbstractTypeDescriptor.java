/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.Locale;

import org.hibernate.models.internal.jdk.JdkPassThruConverter;
import org.hibernate.models.internal.jdk.JdkPassThruExtractor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueConverter;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.rendering.spi.Renderer;
import org.hibernate.models.rendering.spi.RenderingTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;
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
		return JdkPassThruConverter.passThruConverter();
	}

	@Override
	public JdkValueExtractor<V> createJdkValueExtractor(SourceModelBuildingContext modelContext) {
		return JdkPassThruExtractor.passThruExtractor();
	}

	@Override
	public void render(
			String name,
			Object attributeValue,
			RenderingTarget target,
			Renderer renderer,
			SourceModelContext modelContext) {
		target.addLine( "%s = %s", name, attributeValue );
	}

	@Override
	public void render(Object attributeValue, RenderingTarget target, Renderer renderer, SourceModelContext modelContext) {
		target.addLine( "%s", attributeValue );
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
