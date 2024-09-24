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


import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class FromContainersTests {
	@Test
	void testNoGeneratorWithJandex() {
		testNoGenerator( buildJandexIndex( NoGeneratorEntity.class ) );
	}

	@Test
	void testNoGeneratorWithoutJandex() {
		testNoGenerator( null );
	}

	private void testNoGenerator(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		{
			final GeneratorAnnotation found = entityClass.fromSelfAndContainers(
					false,
					buildingContext,
					(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
			);
			assertThat( found ).isNotNull();
			assertThat( found.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );
		}

		{
			final GeneratorAnnotation found = entityClass.findFieldByName( "id" ).fromSelfAndContainers(
					false,
					buildingContext,
					(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
			);
			assertThat( found ).isNotNull();
			assertThat( found.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );
		}
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

		final GeneratorAnnotation fromClass = entityClass.fromSelfAndContainers(
				false,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClass ).isNotNull();
		assertThat( fromClass.value() ).isEqualTo( GeneratorAnnotation.Source.TYPE );

		final GeneratorAnnotation fromMember = idMember.fromSelfAndContainers(
				false,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMember ).isNotNull();
		assertThat( fromMember.value() ).isEqualTo( GeneratorAnnotation.Source.TYPE );
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

		final GeneratorAnnotation fromClass = entityClass.fromSelfAndContainers(
				false,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClass ).isNotNull();
		assertThat( fromClass.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );

		final GeneratorAnnotation fromMember = idMember.fromSelfAndContainers(
				false,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMember ).isNotNull();
		assertThat( fromMember.value() ).isEqualTo( GeneratorAnnotation.Source.MEMBER );
	}

	@Test
	void testUpPackageDefinedWithJandex() {
		testUpPackageDefined( buildJandexIndex( SubNoGeneratorEntity.class ) );
	}

	@Test
	void testUpPackageDefinedWithoutJandex() {
		testUpPackageDefined( null );
	}

	private void testUpPackageDefined(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, SubNoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( SubNoGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// cross package boundary - false

		final GeneratorAnnotation fromClassScoped = entityClass.fromSelfAndContainers(
				false,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClassScoped ).isNull();

		final GeneratorAnnotation fromMemberScoped = idMember.fromSelfAndContainers(
				false,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMemberScoped ).isNull();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// cross package boundary - true

		final GeneratorAnnotation fromClassUnScoped = entityClass.fromSelfAndContainers(
				true,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClassUnScoped ).isNotNull();
		assertThat( fromClassUnScoped.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );

		final GeneratorAnnotation fromMemberUnScoped = idMember.fromSelfAndContainers(
				true,
				buildingContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMemberUnScoped ).isNotNull();
		assertThat( fromMemberUnScoped.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );
	}
}
