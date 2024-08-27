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
 * Descriptor for long values
 *
 * @author Steve Ebersole
 */
public class LongTypeDescriptor extends AbstractTypeDescriptor<Long> {
	public static final LongTypeDescriptor LONG_TYPE_DESCRIPTOR = new LongTypeDescriptor();

	@Override
	public Class<Long> getValueType() {
		return Long.class;
	}

	@Override
	public Object unwrap(Long value) {
		return value;
	}

	@Override
	public void render(
			String name, Object attributeValue, RenderingTarget target,
			Renderer renderer,
			SourceModelContext modelContext) {
		target.addLine( "%s = %sL", name, attributeValue );
	}

	@Override
	public void render(Object attributeValue, RenderingTarget target, Renderer renderer, SourceModelContext modelContext) {
		target.addLine( "%sL", attributeValue );
	}

	@Override
	public Long[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Long[size];
	}
}
