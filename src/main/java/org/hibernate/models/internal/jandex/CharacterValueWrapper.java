/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationValue;

/**
 * Wraps AnnotationValue as a character
 *
 * @author Steve Ebersole
 */
public class CharacterValueWrapper implements ValueWrapper<Character,AnnotationValue> {
	public static final CharacterValueWrapper JANDEX_CHARACTER_VALUE_WRAPPER = new CharacterValueWrapper();

	@Override
	public Character wrap(AnnotationValue rawValue, SourceModelBuildingContext buildingContext) {
		assert rawValue != null;
		return rawValue.asChar();
	}
}
