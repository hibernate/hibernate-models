/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationValue;

/**
 * @author Steve Ebersole
 */
public class EnumValueExtractor<E extends Enum<E>> extends AbstractValueExtractor<E> {
	private final EnumValueConverter<E> wrapper;

	public EnumValueExtractor(EnumValueConverter<E> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected E extractAndWrap(AnnotationValue jandexValue, SourceModelBuildingContext buildingContext) {
		assert jandexValue != null;
		return wrapper.convert( jandexValue, buildingContext );
	}
}
