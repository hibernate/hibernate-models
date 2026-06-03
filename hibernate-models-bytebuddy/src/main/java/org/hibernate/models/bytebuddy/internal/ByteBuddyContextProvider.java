/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.util.Map;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.bytebuddy.Settings;
import org.hibernate.models.internal.BasicModelsContextImpl;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ModelsConfiguration;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.ModelsContextProvider;

import net.bytebuddy.pool.TypePool;

/**
 * @author Steve Ebersole
 */
public class ByteBuddyContextProvider implements ModelsContextProvider {
	public static final ByteBuddyContextProvider BYTEBUDDY_PROVIDER = new ByteBuddyContextProvider();

	@Override
	public ModelsContext produceContext(
			ClassLoading classLoading,
			HibernateAccessorFactory accessorFactory,
			RegistryPrimer registryPrimer,
			Map<Object, Object> configProperties) {
		final TypePool typePool = resolveTypePool( configProperties );
		final boolean trackImplementors = ModelsConfiguration.shouldTrackImplementors( configProperties );

		if ( typePool != null ) {
			return new ByteBuddyModelsContextImpl( typePool, trackImplementors, classLoading, registryPrimer, accessorFactory );
		}

		return new BasicModelsContextImpl( classLoading, accessorFactory, trackImplementors, registryPrimer );
	}

	private TypePool resolveTypePool(Map<Object, Object> configProperties) {
		// todo : do we want to have the ability to create the TypePool or resolve one from another source?
		//		- note: if building, be sure to apply BaseLineJavaTypes
		return (TypePool) configProperties.get( Settings.TYPE_POOL_PARAM );
	}
}
