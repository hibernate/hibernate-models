/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal.values;

import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as an enum
 *
 * @author Steve Ebersole
 */
public class EnumValueConverter<E extends Enum<E>> implements JandexValueConverter<E> {
	private final Class<E> enumClass;

	public EnumValueConverter(Class<E> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public E convert(AnnotationValue jandexValue, SourceModelBuildingContext modelContext) {
		assert jandexValue != null;
		final String enumName = jandexValue.asEnum();
		return Enum.valueOf( enumClass, enumName );
	}
}
