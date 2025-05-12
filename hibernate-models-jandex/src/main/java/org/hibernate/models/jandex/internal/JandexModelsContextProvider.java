/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.util.Map;

import org.hibernate.models.internal.BasicModelsContextImpl;
import org.hibernate.models.jandex.Settings;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ModelsConfiguration;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.ModelsContextProvider;

import org.jboss.jandex.IndexView;

/**
 * ModelsContextProvider for {@code hibernate-models-jandex}
 *
 * @author Steve Ebersole
 */
public class JandexModelsContextProvider implements ModelsContextProvider {
	public static final JandexModelsContextProvider JANDEX_PROVIDER = new JandexModelsContextProvider();

	@Override
	public ModelsContext produceContext(
			ClassLoading classLoading,
			RegistryPrimer registryPrimer,
			Map<Object, Object> configProperties) {
		final IndexView jandexIndex = resolveJandexIndex( configProperties );
		final boolean trackImplementors = ModelsConfiguration.shouldTrackImplementors( configProperties );

		if ( jandexIndex != null ) {
			return new JandexModelsContextImpl( jandexIndex, trackImplementors, classLoading, registryPrimer );
		}

		return new BasicModelsContextImpl( classLoading, trackImplementors, registryPrimer );

	}

	private IndexView resolveJandexIndex(Map<Object, Object> configProperties) {
		// todo : do we want to have the ability to create the Jandex index or resolve one from another source?
		//		- note: if building, be sure to apply BaseLineJavaTypes
		return (IndexView) configProperties.get( Settings.INDEX_PARAM );
	}
}
