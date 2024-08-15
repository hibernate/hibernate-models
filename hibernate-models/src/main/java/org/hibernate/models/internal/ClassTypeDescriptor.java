/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.RenderingCollector;
import org.hibernate.models.spi.SourceModelBuildingContext;

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
			RenderingCollector collector,
			String name,
			Object attributeValue,
			SourceModelBuildingContext modelContext) {
		super.render( collector, name, ( (Class<?>) attributeValue ).getName(), modelContext );
	}

	@Override
	public void render(RenderingCollector collector, Object attributeValue, SourceModelBuildingContext modelContext) {
		super.render( collector, ( (Class<?>) attributeValue ).getName(), modelContext );
	}

	@Override
	public Class<?>[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Class<?>[size];
	}
}
