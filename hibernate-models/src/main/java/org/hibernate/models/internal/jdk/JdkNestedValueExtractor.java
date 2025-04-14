/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ModelsContext;

/**
 * @author Steve Ebersole
 */
public class JdkNestedValueExtractor<A extends Annotation> extends AbstractJdkValueExtractor<A> {
	private final JdkNestedValueConverter<A> converter;

	public JdkNestedValueExtractor(JdkNestedValueConverter<A> converter) {
		this.converter = converter;
	}

	@Override
	protected A wrap(
			A rawValue,
			AttributeDescriptor<A> attributeDescriptor,
			ModelsContext modelsContext) {
		return converter.convert( rawValue, modelsContext );
	}
}
