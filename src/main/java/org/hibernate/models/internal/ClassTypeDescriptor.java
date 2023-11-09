/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.internal.jandex.ClassValueExtractor;
import org.hibernate.models.internal.jandex.ClassValueWrapper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueExtractor;
import org.hibernate.models.spi.ValueWrapper;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.AnnotationValue;

/**
 * Descriptor for class values
 *
 * @author Steve Ebersole
 */
public class ClassTypeDescriptor extends AbstractTypeDescriptor<ClassDetails> {
	public static final ClassTypeDescriptor CLASS_TYPE_DESCRIPTOR = new ClassTypeDescriptor();

	@Override
	public Class<ClassDetails> getWrappedValueType() {
		return ClassDetails.class;
	}

	@Override
	public ValueWrapper<ClassDetails, AnnotationValue> createJandexWrapper(SourceModelBuildingContext buildingContext) {
		return ClassValueWrapper.JANDEX_CLASS_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<AnnotationInstance, ClassDetails> createJandexExtractor(SourceModelBuildingContext buildingContext) {
		return ClassValueExtractor.JANDEX_CLASS_EXTRACTOR;
	}

	@Override
	public ValueWrapper<ClassDetails, ?> createJdkWrapper(SourceModelBuildingContext buildingContext) {
		return org.hibernate.models.internal.jdk.ClassValueWrapper.JDK_CLASS_VALUE_WRAPPER;
	}

	@Override
	public ValueExtractor<Annotation, ClassDetails> createJdkExtractor(SourceModelBuildingContext buildingContext) {
		return org.hibernate.models.internal.jdk.ClassValueExtractor.JDK_CLASS_EXTRACTOR;
	}
}
