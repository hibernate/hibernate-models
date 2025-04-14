/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal.values;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.ModelsContext;

import net.bytebuddy.description.annotation.AnnotationValue;


/**
 * Support for extracting nested annotation values
 *
 * @author Steve Ebersole
 */
public class NestedValueExtractor<A extends Annotation> extends AbstractValueExtractor<A> {
	private final NestedValueConverter<A> wrapper;

	public NestedValueExtractor(NestedValueConverter<A> wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	protected A extractAndWrap(AnnotationValue<?,?> byteBuddyValue, ModelsContext modelsContext) {
		return wrapper.convert( byteBuddyValue, modelsContext );
	}
}
