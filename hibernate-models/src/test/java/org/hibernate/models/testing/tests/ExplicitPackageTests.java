/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.ClassDetailsRegistryStandard;
import org.hibernate.models.internal.MissingPackageInfoDetails;
import org.hibernate.models.internal.SimpleClassLoading;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.annotations.pkg.PackageAnnotation;
import org.hibernate.models.testing.tests.annotations.target.sub.SubNoGeneratorEntity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/// @author Steve Ebersole
class ExplicitPackageTests {
	private static final String PACKAGE_NAME = PackageAnnotation.class.getPackageName();
	private static final String ABSENT_PACKAGE = "does.not.exist";
	private static final String UNDESCRIBED_PACKAGE = SubNoGeneratorEntity.class.getPackageName();

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void descriptorIdentity(boolean explicitFirst) {
		final ModelsContext context = createModelContext();
		final ClassDetailsRegistry registry = context.getClassDetailsRegistry();
		final ClassDetails first = resolve( registry, PACKAGE_NAME, explicitFirst );
		assertThat( first.isRealClass() ).isTrue();
		assertThat( first.getAnnotationUsage( PackageAnnotation.class, context ) ).isNotNull();
		assertThat( resolve( registry, PACKAGE_NAME, !explicitFirst ) ).isSameAs( first );
		assertThat( registry.resolveExplicitPackageDetails( PACKAGE_NAME ) ).isSameAs( first );
		assertThat( registry.resolvePackageDetails( PACKAGE_NAME ) ).isSameAs( first );
		assertCanonical( registry, PACKAGE_NAME, first );
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void missingDescriptors(boolean explicitFirst) {
		assertThat( SIMPLE_CLASS_LOADING.findClassForName( UNDESCRIBED_PACKAGE + ".package-info" ) ).isNull();
		assertThat( SIMPLE_CLASS_LOADING.findClassForName( SubNoGeneratorEntity.class.getName() ) ).isNotNull();
		for ( String packageName : new String[] { ABSENT_PACKAGE, UNDESCRIBED_PACKAGE } ) {
			final ClassDetailsRegistry registry = createModelContext().getClassDetailsRegistry();
			if ( explicitFirst ) {
				assertMissing( registry, packageName );
			}
			final ClassDetails missing = registry.resolvePackageDetails( packageName );
			assertThat( missing ).isInstanceOf( MissingPackageInfoDetails.class );
			assertThat( missing.getDirectAnnotationUsages() ).isEmpty();
			assertMissing( registry, packageName );
			assertMissing( registry, packageName );
			assertThat( registry.resolvePackageDetails( packageName ) ).isSameAs( missing );
			assertCanonical( registry, packageName, missing );
		}
	}

	@Test
	void implicitClassNavigation() {
		final ModelsContext context = createModelContext( SubNoGeneratorEntity.class );
		final ClassDetailsRegistry registry = context.getClassDetailsRegistry();
		final ClassDetails entity = registry.resolveClassDetails( SubNoGeneratorEntity.class.getName() );
		final ClassDetails missing = entity.getContainer( context );
		assertMissing( registry, UNDESCRIBED_PACKAGE );
		assertThat( registry.resolvePackageDetails( UNDESCRIBED_PACKAGE ) ).isSameAs( missing );
		assertThat( entity.getContainer( context ) ).isSameAs( missing );
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void invalidNamesDoNotLoadOrRegister(boolean explicit) {
		final AtomicInteger lookups = new AtomicInteger();
		final SimpleClassLoading loading = new SimpleClassLoading() {
			@Override
			public <T> Class<T> classForName(String name) {
				lookups.incrementAndGet();
				return super.classForName( name );
			}

			@Override
			public URL locateResource(String name) {
				lookups.incrementAndGet();
				return super.locateResource( name );
			}

			@Override
			public <T> Class<T> findClassForName(String name) {
				lookups.incrementAndGet();
				return super.findClassForName( name );
			}
		};
		final ClassDetailsRegistry registry = createModelContext( loading, null ).getClassDetailsRegistry();
		final long initialCount = registry.streamClassDetails().count();
		lookups.set( 0 );
		for ( String name : new String[] { null, "", "package-info", "a.b.package-info" } ) {
			assertThatThrownBy( () -> resolve( registry, name, explicit ) )
					.isInstanceOf( IllegalArgumentException.class );
		}
		assertThat( lookups ).hasValue( 0 );
		assertThat( registry.streamClassDetails().count() ).isEqualTo( initialCount );
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void loadingFailuresPropagate(boolean explicit) {
		for ( Throwable failure : new Throwable[] {
				new UnknownClassException( "Dependency unavailable" ),
				new NoClassDefFoundError( "Dependency unavailable" ),
				new UnsupportedClassVersionError( "Unsupported descriptor version" ),
				new IllegalStateException( "Loading failed" )
		} ) {
			final SimpleClassLoading loading = new SimpleClassLoading() {
				@Override
				public <T> Class<T> findClassForName(String name) {
					if ( name.equals( ABSENT_PACKAGE + ".package-info" ) ) {
						throwFailure( failure );
					}
					return super.findClassForName( name );
				}
			};
			final ClassDetailsRegistry registry = createModelContext( loading, null ).getClassDetailsRegistry();
			assertThatThrownBy( () -> resolve( registry, ABSENT_PACKAGE, explicit ) ).isSameAs( failure );
			assertThat( registry.findPackageDetails( ABSENT_PACKAGE ) ).isNull();
		}
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void constructionFailureIsNotAbsence(boolean explicit) {
		final ModelsContext context = createModelContext();
		final UnknownClassException failure = new UnknownClassException( "Descriptor dependency unavailable" );
		final AtomicInteger discoveries = new AtomicInteger();
		final ClassDetailsRegistry registry = new ClassDetailsRegistryStandard( (name, modelContext) -> {
			assertThat( modelContext.getClassLoading().findClassForName( name ) ).isNotNull();
			discoveries.incrementAndGet();
			throw failure;
		}, false, context );
		assertThatThrownBy( () -> resolve( registry, PACKAGE_NAME, explicit ) ).isSameAs( failure );
		assertThat( discoveries ).hasValue( 1 );
		assertThat( registry.findPackageDetails( PACKAGE_NAME ) ).isNull();
	}

	@Test
	void defaultMethodWorksWithIndependentImplementation() {
		final ClassDetails real = createModelContext().getClassDetailsRegistry().resolvePackageDetails( PACKAGE_NAME );
		for ( ClassDetails result : new ClassDetails[] { real, new MissingPackageInfoDetails( PACKAGE_NAME, PACKAGE_NAME + ".package-info" ) } ) {
			final ClassDetailsRegistry registry = (ClassDetailsRegistry) Proxy.newProxyInstance(
					ClassDetailsRegistry.class.getClassLoader(),
					new Class<?>[] { ClassDetailsRegistry.class },
					(proxy, method, arguments) -> {
						if ( method.isDefault() ) {
							return InvocationHandler.invokeDefault( proxy, method, arguments );
						}
						assertThat( method.getName() ).isEqualTo( "resolvePackageDetails" );
						assertThat( arguments ).containsExactly( PACKAGE_NAME );
						return result;
					}
			);
			if ( result.isRealClass() ) {
				assertThat( registry.resolveExplicitPackageDetails( PACKAGE_NAME ) ).isSameAs( real );
			}
			else {
				assertMissing( registry, PACKAGE_NAME );
			}
		}
	}

	private static ClassDetails resolve(ClassDetailsRegistry registry, String name, boolean explicit) {
		return explicit ? registry.resolveExplicitPackageDetails( name ) : registry.resolvePackageDetails( name );
	}

	private static void assertMissing(ClassDetailsRegistry registry, String name) {
		assertThatThrownBy( () -> registry.resolveExplicitPackageDetails( name ) )
				.isInstanceOf( UnknownClassException.class )
				.hasMessage( "Could not resolve package descriptor '" + name
						+ ".package-info' for explicitly registered package '" + name + "'." );
	}

	private static void assertCanonical(ClassDetailsRegistry registry, String name, ClassDetails details) {
		assertThat( registry.findClassDetails( name ) ).isNull();
		assertThat( registry.findClassDetails( name + ".package-info" ) ).isSameAs( details );
		assertThat( registry.findPackageDetails( name ) ).isSameAs( details );
		assertThat( registry.getPackageDetails( name ) ).isSameAs( details );
	}

	private static void throwFailure(Throwable failure) {
		if ( failure instanceof Error error ) {
			throw error;
		}
		throw (RuntimeException) failure;
	}
}
