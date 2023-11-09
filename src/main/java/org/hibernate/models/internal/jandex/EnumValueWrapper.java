/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as an enum
 *
 * @author Steve Ebersole
 */
public class EnumValueWrapper<E extends Enum<E>> implements ValueWrapper<E,AnnotationValue> {
	private final Class<E> enumClass;

	public EnumValueWrapper(Class<E> enumClass) {
		this.enumClass = enumClass;
	}

	@Override
	public E wrap(AnnotationValue rawValue, AnnotationTarget target, SourceModelBuildingContext buildingContext) {
		assert rawValue != null;
		final String enumName = rawValue.asEnum();
		return Enum.valueOf( enumClass, enumName );
	}
}
