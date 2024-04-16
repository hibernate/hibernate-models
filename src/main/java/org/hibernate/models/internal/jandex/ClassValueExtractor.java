/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class ClassValueExtractor extends AbstractValueExtractor<ClassDetails> {
	public static final ClassValueExtractor JANDEX_CLASS_EXTRACTOR = new ClassValueExtractor();

	@Override
	protected ClassDetails extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return ClassValueWrapper.JANDEX_CLASS_VALUE_WRAPPER.wrap( jandexValue, buildingContext );
	}
}
