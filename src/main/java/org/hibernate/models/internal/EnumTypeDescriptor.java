/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.reflect.Array;

import org.hibernate.models.internal.jandex.EnumValueConverter;
import org.hibernate.models.internal.jandex.EnumValueExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.JandexValueConverter;
import org.hibernate.models.spi.JandexValueExtractor;

/**
 * Descriptor for enum values
 *
 * @author Steve Ebersole
 */
public class EnumTypeDescriptor<E extends Enum<E>> extends AbstractTypeDescriptor<E> {
	private final Class<E> enumType;

	private final EnumValueConverter<E> jandexWrapper;
	private final EnumValueExtractor<E> jandexExtractor;

	public EnumTypeDescriptor(Class<E> enumType) {
		this.enumType = enumType;
		this.jandexWrapper = new EnumValueConverter<>( enumType );
		this.jandexExtractor = new EnumValueExtractor<>( jandexWrapper );
	}

	@Override
	public Class<E> getValueType() {
		return enumType;
	}

	@Override
	public JandexValueConverter<E> createJandexValueConverter(SourceModelBuildingContext modelContext) {
		return jandexWrapper;
	}

	@Override
	public JandexValueExtractor<E> createJandexValueExtractor(SourceModelBuildingContext modelContext) {
		return jandexExtractor;
	}

	@Override
	public Object unwrap(E value) {
		return value;
	}

	@Override
	public E[] makeArray(int size, SourceModelBuildingContext modelContext) {
		//noinspection unchecked
		return (E[]) Array.newInstance( enumType, size );
	}
}
