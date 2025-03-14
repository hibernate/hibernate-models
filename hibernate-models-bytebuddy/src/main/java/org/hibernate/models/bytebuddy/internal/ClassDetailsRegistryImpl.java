/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.util.Map;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.AbstractClassDetailsRegistry;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsRegistryImpl extends AbstractClassDetailsRegistry {
	private final ClassDetailsBuilderImpl classDetailsBuilder;

	public ClassDetailsRegistryImpl(ByteBuddyModelContextImpl context) {
		super( context );
		this.classDetailsBuilder = new ClassDetailsBuilderImpl( context );
	}

	@Override
	public ClassDetailsBuilder getClassDetailsBuilder() {
		return classDetailsBuilder;
	}

	@Override
	protected ClassDetails createClassDetails(String name) {
		final ClassDetails fromByteBuddy = classDetailsBuilder.buildClassDetails( name, context );
		if ( fromByteBuddy != null ) {
			addClassDetails( name, fromByteBuddy );
			return fromByteBuddy;
		}

		final JdkClassDetails jdkClassDetails = JdkBuilders.DEFAULT_BUILDER.buildClassDetails( name, context );
		if ( jdkClassDetails != null ) {
			addClassDetails( name, jdkClassDetails );
			return jdkClassDetails;
		}

		throw new UnknownClassException( "Unable to resolve ClassDetails for `" + name + "`" );
	}

	protected Map<String, ClassDetails> getClassDetailsMap() {
		return classDetailsMap;
	}

}
