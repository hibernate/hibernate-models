/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting enum values
 *
 * @author Steve Ebersole
 */
public class EnumValueExtractor<E extends Enum<E>> extends AbstractValueExtractor<E> {
	private final EnumValueConverter<E> wrapper;

	public EnumValueExtractor(EnumValueConverter<E> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected E extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		assert byteBuddyValue != null;
		return wrapper.convert( byteBuddyValue, modelsContext );
	}
}
