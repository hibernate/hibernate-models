/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jandex.CharacterValueWrapper.JANDEX_CHARACTER_VALUE_WRAPPER;

/**
 * Extracts character values from an attribute
 *
 * @author Steve Ebersole
 */
public class CharacterValueExtractor extends AbstractValueExtractor<Character> {
	public static final CharacterValueExtractor JANDEX_CHARACTER_EXTRACTOR = new CharacterValueExtractor();

	@Override
	protected Character extractAndWrap(
			AnnotationValue jandexValue,
			AnnotationTarget target,
			SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return JANDEX_CHARACTER_VALUE_WRAPPER.wrap( jandexValue, target, buildingContext );
	}
}
