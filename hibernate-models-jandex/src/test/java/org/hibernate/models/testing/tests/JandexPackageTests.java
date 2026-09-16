/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.SimpleClassLoading;
import org.hibernate.models.jandex.internal.JandexModelsContextImpl;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;

import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.Indexer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/// @author Steve Ebersole
class JandexPackageTests {
	private static final String PACKAGE_NAME = "org.hibernate.models.testing.tests.descriptor";
	private static final String DESCRIPTOR_NAME = PACKAGE_NAME + ".package-info";

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void indexedDescriptorNeedsNoReflectiveLoading(boolean explicitFirst) throws IOException {
		final Index index = descriptorIndex();
		final SimpleClassLoading loading = new SimpleClassLoading() {
			@Override
			public <T> Class<T> classForName(String name) {
				assertThat( name ).isNotEqualTo( DESCRIPTOR_NAME );
				return super.classForName( name );
			}

			@Override
			public <T> Class<T> findClassForName(String name) {
				assertThat( name ).isNotEqualTo( DESCRIPTOR_NAME );
				return super.findClassForName( name );
			}
		};
		final JandexModelsContextImpl context = new JandexModelsContextImpl( index, false, loading, null );
		final ClassDetailsRegistry registry = context.getClassDetailsRegistry();
		final ClassDetails first = explicitFirst
				? registry.resolveExplicitPackageDetails( PACKAGE_NAME )
				: registry.resolvePackageDetails( PACKAGE_NAME );
		assertThat( first.wasBuiltFromReflection() ).isFalse();
		assertThat( first.getAnnotationUsage( Deprecated.class, context ) ).isNotNull();
		assertThat( registry.resolvePackageDetails( PACKAGE_NAME ) ).isSameAs( first );
		assertThat( registry.resolveExplicitPackageDetails( PACKAGE_NAME ) ).isSameAs( first );
		assertThat( registry.findClassDetails( DESCRIPTOR_NAME ) ).isSameAs( first );
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void descriptorFallsBackToReflection(boolean explicitFirst) {
		final JandexModelsContextImpl context = new JandexModelsContextImpl(
				new Indexer().complete(), false, SIMPLE_CLASS_LOADING, null
		);
		final ClassDetailsRegistry registry = context.getClassDetailsRegistry();
		final ClassDetails first = explicitFirst
				? registry.resolveExplicitPackageDetails( PACKAGE_NAME )
				: registry.resolvePackageDetails( PACKAGE_NAME );
		assertThat( first.wasBuiltFromReflection() ).isTrue();
		assertThat( registry.resolveExplicitPackageDetails( PACKAGE_NAME ) ).isSameAs( first );
		assertThat( registry.resolvePackageDetails( PACKAGE_NAME ) ).isSameAs( first );
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void indexFailurePropagates(boolean explicit) throws IOException {
		final Index index = descriptorIndex();
		final UnknownClassException failure = new UnknownClassException( "Index lookup failed" );
		final IndexView failingIndex = (IndexView) Proxy.newProxyInstance(
				IndexView.class.getClassLoader(), new Class<?>[] { IndexView.class },
				(proxy, method, arguments) -> {
					if ( method.getName().equals( "getClassByName" )
							&& arguments[0].toString().equals( DESCRIPTOR_NAME ) ) {
						throw failure;
					}
					try {
						return method.invoke( index, arguments );
					}
					catch (InvocationTargetException e) {
						throw e.getCause();
					}
				}
		);
		final ClassDetailsRegistry registry = new JandexModelsContextImpl(
				failingIndex, false, SIMPLE_CLASS_LOADING, null
		).getClassDetailsRegistry();
		assertThatThrownBy( () -> {
			if ( explicit ) {
				registry.resolveExplicitPackageDetails( PACKAGE_NAME );
			}
			else {
				registry.resolvePackageDetails( PACKAGE_NAME );
			}
		} ).isSameAs( failure );
		assertThat( registry.findPackageDetails( PACKAGE_NAME ) ).isNull();
	}

	private static Index descriptorIndex() throws IOException {
		final Indexer indexer = new Indexer();
		indexer.indexClass( Deprecated.class );
		indexer.indexClass( SIMPLE_CLASS_LOADING.classForName( DESCRIPTOR_NAME ) );
		return indexer.complete();
	}
}
