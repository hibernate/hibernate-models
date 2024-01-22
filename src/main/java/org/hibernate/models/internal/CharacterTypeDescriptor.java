/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.CharacterValueExtractor;
import org.hibernate.models.internal.jandex.CharacterValueWrapper;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jdk.PassThruWrapper.PASS_THRU_WRAPPER;

/**
 * Descriptor for char values
 *
 * @author Steve Ebersole
 */
public class CharacterTypeDescriptor extends AbstractTypeDescriptor<Character> {
	public static final CharacterTypeDescriptor CHARACTER_TYPE_DESCRIPTOR = new CharacterTypeDescriptor();

	@Override
	public Class<Character> getWrappedValueType() {
		return Character.class;
	}

	@Override
	public ValueWrapper<Character, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return CharacterValueWrapper.JANDEX_CHARACTER_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, Character> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return CharacterValueExtractor.JANDEX_CHARACTER_EXTRACTOR;
	}

	@Override
	public ValueWrapper<Character, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PASS_THRU_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, Character> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PassThruExtractor.PASS_THRU_EXTRACTOR;
	}

	@Override
	public Object unwrap(Character value) {
		return value;
	}
}
