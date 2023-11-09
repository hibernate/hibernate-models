/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.ShortValueExtractor;
import org.hibernate.models.internal.jandex.ShortValueWrapper;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jdk.PassThruWrapper.PASS_THRU_WRAPPER;

/**
 * Descriptor for short values
 *
 * @author Steve Ebersole
 */
public class ShortTypeDescriptor extends AbstractTypeDescriptor<Short> {
	public static final ShortTypeDescriptor SHORT_TYPE_DESCRIPTOR = new ShortTypeDescriptor();

	@Override
	public Class<Short> getWrappedValueType() {
		return Short.class;
	}

	@Override
	public ValueWrapper<Short, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return ShortValueWrapper.JANDEX_SHORT_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, Short> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return ShortValueExtractor.JANDEX_SHORT_EXTRACTOR;
	}

	@Override
	public ValueWrapper<Short, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PASS_THRU_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, Short> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PassThruExtractor.PASS_THRU_EXTRACTOR;
	}
}
