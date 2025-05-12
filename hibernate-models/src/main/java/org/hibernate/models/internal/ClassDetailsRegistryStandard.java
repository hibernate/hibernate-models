/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;


import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ModelsContext;

/**
 * Standard ClassDetailsRegistry implementation.
 *
 * @author Steve Ebersole
 */
public class ClassDetailsRegistryStandard
		extends AbstractClassDetailsRegistry {
	private final ClassDetailsBuilder classDetailsBuilder;

	public ClassDetailsRegistryStandard(boolean trackImplementors, ModelsContext context) {
		this( JdkBuilders.DEFAULT_BUILDER, trackImplementors, context );
	}

	public ClassDetailsRegistryStandard(ClassDetailsBuilder classDetailsBuilder, boolean trackImplementors, ModelsContext context) {
		super( trackImplementors, context );
		this.classDetailsBuilder = classDetailsBuilder;
	}

	@Override
	public ClassDetailsBuilder getClassDetailsBuilder() {
		return classDetailsBuilder;
	}
}
