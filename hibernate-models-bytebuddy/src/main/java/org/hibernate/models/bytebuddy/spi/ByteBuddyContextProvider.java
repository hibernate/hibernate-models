/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.spi;

import java.util.Map;

import org.hibernate.models.bytebuddy.Settings;
import org.hibernate.models.bytebuddy.internal.ByteBuddyModelContextImpl;
import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelBuildingContextProvider;

import net.bytebuddy.pool.TypePool;

/**
 * @author Steve Ebersole
 */
public class ByteBuddyContextProvider implements SourceModelBuildingContextProvider {
	public static final ByteBuddyContextProvider BYTEBUDDY_PROVIDER = new ByteBuddyContextProvider();

	@Override
	public SourceModelBuildingContext produceContext(
			ClassLoading classLoading,
			RegistryPrimer registryPrimer,
			Map<Object, Object> configProperties) {
		final TypePool typePool = resolveTypePool( configProperties );

		if ( typePool != null ) {
			return new ByteBuddyModelContextImpl( typePool, classLoading, registryPrimer );
		}

		return new BasicModelBuildingContextImpl( classLoading, registryPrimer );
	}

	private TypePool resolveTypePool(Map<Object, Object> configProperties) {
		// todo : do we want to have the ability to create the TypePool or resolve one from another source?
		//		- note: if building, be sure to apply BaseLineJavaTypes
		return (TypePool) configProperties.get( Settings.TYPE_POOL_PARAM );
	}
}
