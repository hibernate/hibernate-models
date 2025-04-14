/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.annotations.pkg.PackageAnnotation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
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
}
