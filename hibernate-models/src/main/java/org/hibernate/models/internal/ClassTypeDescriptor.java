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
 * Descriptor for class values
 *
 * @author Steve Ebersole
 */
public class ClassTypeDescriptor extends AbstractTypeDescriptor<Class<?>> {
	public static final ClassTypeDescriptor CLASS_TYPE_DESCRIPTOR = new ClassTypeDescriptor();

	@Override
	public Class<Class<?>> getValueType() {
		//noinspection unchecked,rawtypes
		return (Class) Class.class;
	}

	@Override
	public Object unwrap(Class<?> value) {
		return value;
	}

	@Override
	public void render(
			String name,
			Object attributeValue,
			RenderingTarget target,
			Renderer renderer,
			SourceModelContext modelContext) {
		super.render( name, ( (Class<?>) attributeValue ).getName(), target, renderer, modelContext );
	}

	@Override
	public void render(Object attributeValue, RenderingTarget target, Renderer renderer, SourceModelContext modelContext) {
		super.render( ( (Class<?>) attributeValue ).getName(), target, renderer, modelContext );
	}

	@Override
	public Class<?>[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Class<?>[size];
	}
}
