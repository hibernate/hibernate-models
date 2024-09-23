/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations.target;

import org.hibernate.models.annotations.target.sub.SubNoGeneratorEntity;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;
import org.jboss.jandex.IndexView;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class AnnotationTargetTests {
	@Test
	void testPackageDefinedWithJandex() {
		testPackageDefined( buildJandexIndex( NoGeneratorEntity.class ) );
	}

	@Test
	void testPackageDefinedWithoutJandex() {
		testPackageDefined( null );
	}

	/**
	 * We should find the annotation on the package
	 */
	void testPackageDefined(IndexView jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( buildingContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isFalse();

		final ClassDetails idMemberContainer = idMember.getContainer( buildingContext );
		assertThat( idMemberContainer ).isSameAs( entityClass );
		assertThat( idMemberContainer.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMemberContainer.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isFalse();

		final ClassDetails entityClassPackage = entityClass.getContainer( buildingContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isTrue();
	}

	@Test
	void testClassDefinedWithJandex() {
		testClassDefined( buildJandexIndex( ClassGeneratorEntity.class ) );
	}

	@Test
	void testClassDefinedWithoutJandex() {
		testClassDefined( null );
	}

	private void testClassDefined(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, ClassGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( ClassGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( buildingContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isFalse();

		assertThat( entityClass.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClass.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isTrue();

		final ClassDetails entityClassPackage = entityClass.getContainer( buildingContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isTrue();
	}

	@Test
	void testMemberDefinedWithJandex() {
		testMemberDefined( buildJandexIndex( MemberGeneratorEntity.class ) );
	}

	@Test
	void testMemberDefinedWithoutJandex() {
		testMemberDefined( null );
	}

	private void testMemberDefined(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, MemberGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( MemberGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( buildingContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isTrue();

		assertThat( entityClass.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( entityClass.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isFalse();

		final ClassDetails entityClassPackage = entityClass.getContainer( buildingContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isTrue();
	}

	@Test
	void testUpPackageDefinedWithJandex() {
		testUpPackageDefined( buildJandexIndex( SubNoGeneratorEntity.class ) );
	}

	@Test
	void testUpPackageDefinedWithoutJandex() {
		testUpPackageDefined( null );
	}

	/**
	 */
	void testUpPackageDefined(IndexView jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, SubNoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( SubNoGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		assertThat( idMember.getContainer( buildingContext ) ).isSameAs( entityClass );

		assertThat( idMember.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMember.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isFalse();

		final ClassDetails idMemberContainer = idMember.getContainer( buildingContext );
		assertThat( idMemberContainer ).isSameAs( entityClass );
		assertThat( idMemberContainer.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( idMemberContainer.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isFalse();

		final ClassDetails entityClassPackage = entityClass.getContainer( buildingContext );
		assertThat( entityClassPackage.getName() ).endsWith( "annotations.target.sub.package-info" );
		assertThat( entityClassPackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isFalse();
		assertThat( entityClassPackage.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isFalse();

		final ClassDetails entityClassPackagePackage = entityClassPackage.getContainer( buildingContext );
		assertThat( entityClassPackagePackage.getName() ).endsWith( "annotations.target.package-info" );
		assertThat( entityClassPackagePackage.hasDirectAnnotationUsage( GeneratorAnnotation.class ) ).isTrue();
		assertThat( entityClassPackagePackage.hasAnnotationUsage( GeneratorAnnotation.class, buildingContext ) ).isTrue();
	}

}
