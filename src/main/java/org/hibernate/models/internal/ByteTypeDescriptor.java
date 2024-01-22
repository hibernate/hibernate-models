/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.ByteValueExtractor;
import org.hibernate.models.internal.jandex.ByteValueWrapper;
import org.hibernate.models.internal.jdk.PassThruExtractor;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

import static org.hibernate.models.internal.jdk.PassThruWrapper.PASS_THRU_WRAPPER;

/**
 * Descriptor for byte values
 *
 * @author Steve Ebersole
 */
public class ByteTypeDescriptor extends AbstractTypeDescriptor<Byte> {
	public static final ByteTypeDescriptor BYTE_TYPE_DESCRIPTOR = new ByteTypeDescriptor();

	@Override
	public Class<Byte> getWrappedValueType() {
		return Byte.class;
	}

	@Override
	public ValueWrapper<Byte, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return ByteValueWrapper.JANDEX_BYTE_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, Byte> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return ByteValueExtractor.JANDEX_BYTE_EXTRACTOR;
	}

	@Override
	public ValueWrapper<Byte, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PASS_THRU_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, Byte> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		//noinspection unchecked
		return PassThruExtractor.PASS_THRU_EXTRACTOR;
	}

	@Override
	public Object unwrap(Byte value) {
		return value;
	}
}
