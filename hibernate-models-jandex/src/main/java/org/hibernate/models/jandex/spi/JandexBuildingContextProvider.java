/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.jandex.spi;

import java.util.Map;

import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelBuildingContextProvider;

import org.jboss.jandex.IndexView;

import org.hibernate.models.jandex.internal.JandexModelBuildingContextImpl;

/**
 * @author Steve Ebersole
 */
public class JandexBuildingContextProvider implements SourceModelBuildingContextProvider {
	public static final String INDEX_PARAM = "hibernate.models.jandex.index";

	@Override
	public SourceModelBuildingContext produceContext(
			ClassLoading classLoading,
			RegistryPrimer registryPrimer,
			Map<Object, Object> configProperties) {
		final IndexView jandexIndex = resolveJandexIndex( configProperties );

		if ( jandexIndex != null ) {
			return new JandexModelBuildingContextImpl( jandexIndex, classLoading, registryPrimer );
		}

		return new BasicModelBuildingContextImpl( classLoading, registryPrimer );

	}

	private IndexView resolveJandexIndex(Map<Object, Object> configProperties) {
		// todo : do we want to have the ability to create the Jandex index or resolve one from another source?
		//		- note: if building, be sure to apply BaseLineJavaTypes
		return (IndexView) configProperties.get( INDEX_PARAM );
	}
}
