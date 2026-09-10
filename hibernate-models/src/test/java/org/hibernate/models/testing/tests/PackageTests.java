/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.util.concurrent.atomic.AtomicInteger;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.annotations.pkg.PackageAnnotation;
import org.hibernate.models.testing.tests.annotations.target.sub.SubNoGeneratorEntity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
@SuppressWarnings("removal")
public class PackageTests {
	private static final String PACKAGE_NAME = PackageAnnotation.class.getPackageName();

	@Test
	void testExactReference() {
		final ModelsContext modelsContext = createModelContext();
		final String packageInfoName = PACKAGE_NAME + ".package-info";
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( packageInfoName );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getClassName() ).endsWith( "package-info" );
		assertThat( classDetails.getAnnotationUsage( PackageAnnotation.class, modelsContext ) ).isNotNull();
		assertThat( modelsContext.getClassDetailsRegistry().findPackageDetails( PACKAGE_NAME ) ).isSameAs( classDetails );
		assertThat( modelsContext.getClassDetailsRegistry().getPackageDetails( PACKAGE_NAME ) ).isSameAs( classDetails );
	}

	@Test
	void testPackageReference() {
		final ModelsContext modelsContext = createModelContext();
		assertThatThrownBy( () -> modelsContext.getClassDetailsRegistry().resolveClassDetails( PACKAGE_NAME ) )
				.isInstanceOf( UnknownClassException.class );
	}

	@Test
	void testCreatorDoesNotAttemptPackageFallback() {
		final ModelsContext modelsContext = createModelContext();
		final MutableClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry()
				.as( MutableClassDetailsRegistry.class );
		final AtomicInteger invocationCount = new AtomicInteger();

		assertThatThrownBy( () -> classDetailsRegistry.resolveClassDetails( PACKAGE_NAME, name -> {
			invocationCount.incrementAndGet();
			throw new UnknownClassException( name );
		}) ).isInstanceOf( UnknownClassException.class );
		assertThat( invocationCount ).hasValue( 1 );
	}

	@Test
	void testClassOrPackageReference() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final String packageInfoName = PACKAGE_NAME + ".package-info";

		final ClassDetails classDetails = classDetailsRegistry.resolveClassOrPackageDetails( PACKAGE_NAME );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getClassName() ).isEqualTo( packageInfoName );
		assertThat( classDetails.getAnnotationUsage( PackageAnnotation.class, modelsContext ) ).isNotNull();
		assertThat( classDetailsRegistry.findClassDetails( PACKAGE_NAME ) ).isNull();
		assertThat( classDetailsRegistry.findClassDetails( packageInfoName ) ).isSameAs( classDetails );
		assertThat( classDetailsRegistry.resolveClassOrPackageDetails( packageInfoName ) ).isSameAs( classDetails );
	}

	@Test
	void testResolvePackageReference() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final String packageInfoName = PACKAGE_NAME + ".package-info";

		assertThat( classDetailsRegistry.findPackageDetails( PACKAGE_NAME ) ).isNull();
		assertThatThrownBy( () -> classDetailsRegistry.getPackageDetails( PACKAGE_NAME ) )
				.isInstanceOf( UnknownClassException.class );

		final ClassDetails packageDetails = classDetailsRegistry.resolvePackageDetails( PACKAGE_NAME );
		assertThat( packageDetails.getClassName() ).isEqualTo( packageInfoName );
		assertThat( packageDetails.getAnnotationUsage( PackageAnnotation.class, modelsContext ) ).isNotNull();
		assertThat( classDetailsRegistry.findClassDetails( PACKAGE_NAME ) ).isNull();
		assertThat( classDetailsRegistry.findClassDetails( packageInfoName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.findPackageDetails( PACKAGE_NAME ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.getPackageDetails( PACKAGE_NAME ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.resolvePackageDetails( PACKAGE_NAME ) ).isSameAs( packageDetails );
	}

	@Test
	void testResolvePackageWithoutPackageInfo() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final String packageName = "does.not.exist";
		final String packageInfoName = packageName + ".package-info";

		final ClassDetails packageDetails = classDetailsRegistry.resolvePackageDetails( packageName );
		assertThat( packageDetails.isRealClass() ).isFalse();
		assertThat( packageDetails.getClassName() ).isEqualTo( packageInfoName );
		assertThat( classDetailsRegistry.findClassDetails( packageName ) ).isNull();
		assertThat( classDetailsRegistry.findClassDetails( packageInfoName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.findPackageDetails( packageName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.getPackageDetails( packageName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.resolvePackageDetails( packageName ) ).isSameAs( packageDetails );
	}

	@Test
	void testPackageResolutionDoesNotPreferClass() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final String ambiguousName = PackageAnnotation.class.getName();

		final ClassDetails classDetails = classDetailsRegistry.resolveClassOrPackageDetails( ambiguousName );
		final ClassDetails packageDetails = classDetailsRegistry.resolvePackageDetails( ambiguousName );

		assertThat( classDetails.getClassName() ).isEqualTo( ambiguousName );
		assertThat( packageDetails.getClassName() ).isEqualTo( ambiguousName + ".package-info" );
		assertThat( packageDetails ).isNotSameAs( classDetails );
	}

	@Test
	void testInvalidPackageNames() {
		final ClassDetailsRegistry classDetailsRegistry = createModelContext().getClassDetailsRegistry();

		assertThatThrownBy( () -> classDetailsRegistry.resolvePackageDetails( null ) )
				.isInstanceOf( IllegalArgumentException.class );
		assertThatThrownBy( () -> classDetailsRegistry.resolvePackageDetails( "" ) )
				.isInstanceOf( IllegalArgumentException.class );
		assertThatThrownBy( () -> classDetailsRegistry.resolvePackageDetails( "a.b.package-info" ) )
				.isInstanceOf( IllegalArgumentException.class );
		assertThatThrownBy( () -> classDetailsRegistry.findPackageDetails( "a.b.package-info" ) )
				.isInstanceOf( IllegalArgumentException.class );
		assertThatThrownBy( () -> classDetailsRegistry.getPackageDetails( "a.b.package-info" ) )
				.isInstanceOf( IllegalArgumentException.class );
	}

	@Test
	void testClassOrPackageReferencePrefersClass() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry()
				.resolveClassOrPackageDetails( PackageAnnotation.class.getName() );

		assertThat( classDetails.getClassName() ).isEqualTo( PackageAnnotation.class.getName() );
	}

	@Test
	void testUnverifiedPackageReference() {
		final ModelsContext modelsContext = createModelContext( SubNoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final String packageName = SubNoGeneratorEntity.class.getPackageName();

		assertThatThrownBy( () -> classDetailsRegistry.resolveClassOrPackageDetails( packageName ) )
				.isInstanceOf( UnknownClassException.class );
		assertThatThrownBy( () -> classDetailsRegistry.resolveClassOrPackageDetails( packageName + ".package-info" ) )
				.isInstanceOf( UnknownClassException.class );
		assertThatThrownBy( () -> classDetailsRegistry.resolveClassOrPackageDetails( "does.not.exist" ) )
				.isInstanceOf( UnknownClassException.class );
	}

	@Test
	void testMissingPackageInfoCreatedByContainerResolution() {
		final ModelsContext modelsContext = createModelContext( SubNoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final ClassDetails entityDetails = classDetailsRegistry.resolveClassDetails( SubNoGeneratorEntity.class.getName() );
		final String packageName = SubNoGeneratorEntity.class.getPackageName();
		final String packageInfoName = packageName + ".package-info";

		final ClassDetails packageDetails = entityDetails.getContainer( modelsContext );
		assertThat( packageDetails ).isNotNull();
		assertThat( packageDetails.isRealClass() ).isFalse();
		assertThat( packageDetails.getClassName() ).isEqualTo( packageInfoName );
		assertThat( classDetailsRegistry.findClassDetails( packageName ) ).isNull();
		assertThat( classDetailsRegistry.findClassDetails( packageInfoName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.findPackageDetails( packageName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.getPackageDetails( packageName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.resolvePackageDetails( packageName ) ).isSameAs( packageDetails );
		assertThat( entityDetails.getContainer( modelsContext ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.resolveClassOrPackageDetails( packageName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.resolveClassOrPackageDetails( packageInfoName ) ).isSameAs( packageDetails );
	}

	@Test
	void testGetPackageFromClassWithPackageInfo() {
		final ModelsContext modelsContext = createModelContext( PackageAnnotation.class );
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( PackageAnnotation.class.getName() );
		final ClassDetails pkg = classDetails.getPackage();
		assertThat( pkg ).isNotNull();
		assertThat( pkg.getClassName() ).isEqualTo( PACKAGE_NAME + ".package-info" );
		assertThat( pkg.getAnnotationUsage( PackageAnnotation.class, modelsContext ) ).isNotNull();
	}

	@Test
	void testGetPackageFromClassWithoutPackageInfo() {
		final ModelsContext modelsContext = createModelContext( PackageTests.class );
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( PackageTests.class.getName() );
		final ClassDetails pkg = classDetails.getPackage();
		assertThat( pkg ).isNotNull();
		assertThat( pkg.getClassName() ).endsWith( "package-info" );
	}

	@Test
	void testGetPackageFromDefaultPackageClass() {
		assertThat( ClassDetails.VOID_CLASS_DETAILS.getPackage() ).isNull();
	}

	@Test
	void testGetPackageFromPackageInfo() {
		final ModelsContext modelsContext = createModelContext();
		final String packageInfoName = PACKAGE_NAME + ".package-info";
		final ClassDetails packageInfoDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( packageInfoName );
		final ClassDetails parentPkg = packageInfoDetails.getPackage();
		if ( parentPkg != null ) {
			assertThat( parentPkg.getClassName() )
					.isEqualTo( "org.hibernate.models.testing.annotations.package-info" );
		}
	}
}
