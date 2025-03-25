/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.util.Map;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.AbstractClassDetailsRegistry;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.IndexView;

/**
 * ClassDetailsRegistry using Jandex
 *
 * @author Steve Ebersole
 */
public class JandexClassDetailsRegistry extends AbstractClassDetailsRegistry {
	private final IndexView jandexIndex;
	private final ClassDetailsBuilder classDetailsBuilder;

	public JandexClassDetailsRegistry(IndexView jandexIndex, SourceModelBuildingContext context) {
		super( context );
		this.jandexIndex = jandexIndex;
		this.classDetailsBuilder = new JandexClassDetailsBuilderImpl( jandexIndex, context );
	}

	public IndexView getJandexIndex() {
		return jandexIndex;
	}

	@Override
	public ClassDetailsBuilder getClassDetailsBuilder() {
		return classDetailsBuilder;
	}

	@Override
	protected ClassDetails createClassDetails(String name) {
		final ClassDetails fromJandex = classDetailsBuilder.buildClassDetails( name, context );
		if ( fromJandex != null ) {
			addClassDetails( name, fromJandex );
			return fromJandex;
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
