/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.JdkValueExtractor;
import org.hibernate.models.spi.ModelsContext;

/**
 * @author Steve Ebersole
 */
public class JdkPassThruExtractor<V> extends AbstractJdkValueExtractor<V> {
	@SuppressWarnings("rawtypes")
	public static final JdkPassThruExtractor PASS_THRU_EXTRACTOR = new JdkPassThruExtractor();

	public static <V> JdkValueExtractor<V> passThruExtractor() {
		//noinspection unchecked
		return PASS_THRU_EXTRACTOR;
	}

	@Override
	protected V wrap(
			V rawValue,
			AttributeDescriptor<V> attributeDescriptor,
			ModelsContext modelsContext) {
		return rawValue;
	}
}
