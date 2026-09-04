/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import org.hibernate.models.UnknownClassException;
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
	}

	@Test
	void testPackageReference() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( PACKAGE_NAME );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getClassName() ).endsWith( "package-info" );
		assertThat( classDetails.getAnnotationUsage( PackageAnnotation.class, modelsContext ) ).isNotNull();
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
		assertThat( entityDetails.getContainer( modelsContext ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.resolveClassOrPackageDetails( packageName ) ).isSameAs( packageDetails );
		assertThat( classDetailsRegistry.resolveClassOrPackageDetails( packageInfoName ) ).isSameAs( packageDetails );
	}
}
