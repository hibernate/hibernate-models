/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations.target;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.tests.annotations.target.sub.SubNoGeneratorEntity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class AnnotationTargetTests {
	/**
	 * We should find the annotation on the package
	 */
	@Test
	void testPackageDefined() {
		final ModelsContext modelsContext = createModelContext( NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( modelsContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isFalse();

		final ClassDetails idMemberContainer = idMember.getContainer( modelsContext );
		assertThat( idMemberContainer ).isSameAs( entityClass );
		assertThat( idMemberContainer.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMemberContainer.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isFalse();

		final ClassDetails entityClassPackage = entityClass.getContainer( modelsContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isTrue();
	}

	@Test
	void testClassDefined() {
		final ModelsContext modelsContext = createModelContext( ClassGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( ClassGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( modelsContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isFalse();

		assertThat( entityClass.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClass.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isTrue();

		final ClassDetails entityClassPackage = entityClass.getContainer( modelsContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isTrue();
	}

	@Test
	void testMemberDefined() {
		final ModelsContext modelsContext = createModelContext( MemberGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( MemberGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( modelsContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isTrue();

		assertThat( entityClass.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( entityClass.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isFalse();

		final ClassDetails entityClassPackage = entityClass.getContainer( modelsContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isTrue();
	}

	@Test
	void testUpPackageDefined() {
		final ModelsContext modelsContext = createModelContext( SubNoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( SubNoGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( modelsContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isFalse();

		final ClassDetails idMemberContainer = idMember.getContainer( modelsContext );
		assertThat( idMemberContainer ).isSameAs( entityClass );
		assertThat( idMemberContainer.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMemberContainer.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isFalse();

		final ClassDetails entityClassPackage = entityClass.getContainer( modelsContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.sub.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isFalse();

		final ClassDetails entityClassPackagePackage = entityClassPackage.getContainer( modelsContext );
		assertThat( entityClassPackagePackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackagePackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackagePackage.hasAnnotationUsage( GeneratorAnnotation.class, modelsContext ) ).isTrue();
	}

}
