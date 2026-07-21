/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;

/**
 * Support for extracting array values
 *
 * @author Steve Ebersole
 */
public class ArrayValueExtractor extends AbstractValueExtractor<Object> {
	private final ValueConverter<Object> wrapper;

	public ArrayValueExtractor(ValueConverter<Object> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected Object extractAndWrap(AnnotationValue<?, ?> byteBuddyValue, ModelsContext modelsContext) {
		return wrapper.convert( byteBuddyValue, modelsContext );
	}
}
