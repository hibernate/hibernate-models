/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import org.hibernate.models.bytebuddy.Settings;
import org.hibernate.models.bytebuddy.internal.ByteBuddyModelContextImpl;
import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.spi.ModelsConfiguration;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.testing.shared.intg.ByteBuddyModelContextFactory;

import org.junit.jupiter.api.Test;

import net.bytebuddy.pool.TypePool;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.bytebuddy.spi.ByteBuddyContextProvider.BYTEBUDDY_PROVIDER;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class ProviderTests {
	@Test
	void testBasicBootstrap() {
		final SourceModelBuildingContext context = new ModelsConfiguration()
				.bootstrap();
		assertThat( context ).isNotNull();
		assertThat( context ).isInstanceOf( BasicModelBuildingContextImpl.class );
	}

	@Test
	void testExplicitProvider() {
		final SourceModelBuildingContext context = new ModelsConfiguration()
				.setExplicitContextProvider( BYTEBUDDY_PROVIDER )
				.bootstrap();
		assertThat( context ).isNotNull();
		// without passing the TypePool, the basic one is used
		assertThat( context ).isInstanceOf( BasicModelBuildingContextImpl.class );
	}

	@Test
	void testPassingJandexIndex() {
		final TypePool typePool = ByteBuddyModelContextFactory.buildTypePool( SIMPLE_CLASS_LOADING );
		final SourceModelBuildingContext context = new ModelsConfiguration()
				.configValue( Settings.TYPE_POOL_PARAM, typePool )
				.bootstrap();
		assertThat( context ).isNotNull();
		assertThat( context ).isInstanceOf( ByteBuddyModelContextImpl.class );
	}
}
