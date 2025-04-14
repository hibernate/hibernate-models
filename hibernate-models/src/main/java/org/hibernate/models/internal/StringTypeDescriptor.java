/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.rendering.spi.Renderer;
import org.hibernate.models.rendering.spi.RenderingTarget;
import org.hibernate.models.spi.ModelsContext;

/**
 * Descriptor for string values
 *
 * @author Steve Ebersole
 */
public class StringTypeDescriptor extends AbstractTypeDescriptor<String> {
	public static final StringTypeDescriptor STRING_TYPE_DESCRIPTOR = new StringTypeDescriptor();

	@Override
	public Class<String> getValueType() {
		return String.class;
	}

	@Override
	public Object unwrap(String value) {
		return value;
	}

	@Override
	public void render(
			String name, Object attributeValue, RenderingTarget target,
			Renderer renderer, ModelsContext modelContext) {
		target.addLine( "%s = \"%s\"", name, attributeValue );
	}

	@Override
	public void render(Object attributeValue, RenderingTarget target, Renderer renderer, ModelsContext modelContext) {
		target.addLine( "\"%s\"", attributeValue );
	}

	@Override
	public String[] makeArray(int size, ModelsContext modelContext) {
		return new String[size];
	}
}
