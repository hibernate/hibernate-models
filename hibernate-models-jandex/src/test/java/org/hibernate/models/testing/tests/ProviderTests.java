/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import org.hibernate.models.internal.BasicModelBuildingContextImpl;
import org.hibernate.models.jandex.Settings;
import org.hibernate.models.jandex.internal.JandexModelBuildingContextImpl;
import org.hibernate.models.spi.ModelsConfiguration;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.testing.shared.intg.JandexModelContextFactoryImpl;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.jandex.spi.JandexBuildingContextProvider.JANDEX_PROVIDER;

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
				.setExplicitContextProvider( JANDEX_PROVIDER )
				.bootstrap();
		assertThat( context ).isNotNull();
		// without passing the Index, the basic one is used
		assertThat( context ).isInstanceOf( BasicModelBuildingContextImpl.class );
	}

	@Test
	void testPassingJandexIndex() {
		final Index index = JandexModelContextFactoryImpl.buildJandexIndex( SIMPLE_CLASS_LOADING );
		final SourceModelBuildingContext context = new ModelsConfiguration()
				.configValue( Settings.INDEX_PARAM, index )
				.bootstrap();
		assertThat( context ).isNotNull();
		assertThat( context ).isInstanceOf( JandexModelBuildingContextImpl.class );
	}
}
