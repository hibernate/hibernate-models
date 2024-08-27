/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.rendering.spi.Renderer;
import org.hibernate.models.rendering.spi.RenderingTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;

/**
 * Descriptor for float values
 *
 * @author Steve Ebersole
 */
public class FloatTypeDescriptor extends AbstractTypeDescriptor<Float> {
	public static final FloatTypeDescriptor FLOAT_TYPE_DESCRIPTOR = new FloatTypeDescriptor();

	@Override
	public Class<Float> getValueType() {
		return Float.class;
	}

	@Override
	public Object unwrap(Float value) {
		return value;
	}

	@Override
	public void render(
			String name, Object attributeValue, RenderingTarget target,
			Renderer renderer,
			SourceModelContext modelContext) {
		target.addLine( "%s = %sF", name, attributeValue );
	}

	@Override
	public void render(Object attributeValue, RenderingTarget target, Renderer renderer, SourceModelContext modelContext) {
		target.addLine( "%sF", attributeValue );
	}

	@Override
	public Float[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Float[size];
	}
}
