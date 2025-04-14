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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class FromContainersTests {
	@Test
	void testNoGenerator() {
		final ModelsContext modelsContext = createModelContext( NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		{
			final GeneratorAnnotation found = entityClass.fromSelfAndContainers(
					false,
					modelsContext,
					(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
			);
			assertThat( found ).isNotNull();
			assertThat( found.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );
		}

		{
			final GeneratorAnnotation found = entityClass.findFieldByName( "id" ).fromSelfAndContainers(
					false,
					modelsContext,
					(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
			);
			assertThat( found ).isNotNull();
			assertThat( found.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );
		}
	}

	@Test
	void testClassDefined() {
		final ModelsContext modelsContext = createModelContext( ClassGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( ClassGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );

		final GeneratorAnnotation fromClass = entityClass.fromSelfAndContainers(
				false,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClass ).isNotNull();
		assertThat( fromClass.value() ).isEqualTo( GeneratorAnnotation.Source.TYPE );

		final GeneratorAnnotation fromMember = idMember.fromSelfAndContainers(
				false,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMember ).isNotNull();
		assertThat( fromMember.value() ).isEqualTo( GeneratorAnnotation.Source.TYPE );
	}

	@Test
	void testMemberDefined() {
		final ModelsContext modelsContext = createModelContext( MemberGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( MemberGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );

		final GeneratorAnnotation fromClass = entityClass.fromSelfAndContainers(
				false,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClass ).isNotNull();
		assertThat( fromClass.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );

		final GeneratorAnnotation fromMember = idMember.fromSelfAndContainers(
				false,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMember ).isNotNull();
		assertThat( fromMember.value() ).isEqualTo( GeneratorAnnotation.Source.MEMBER );
	}

	@Test
	void testUpPackageDefined() {
		final ModelsContext modelsContext = createModelContext( SubNoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( SubNoGeneratorEntity.class.getName() );
		final FieldDetails idMember = entityClass.findFieldByName( "id" );

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// cross package boundary - false

		final GeneratorAnnotation fromClassScoped = entityClass.fromSelfAndContainers(
				false,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClassScoped ).isNull();

		final GeneratorAnnotation fromMemberScoped = idMember.fromSelfAndContainers(
				false,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMemberScoped ).isNull();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// cross package boundary - true

		final GeneratorAnnotation fromClassUnScoped = entityClass.fromSelfAndContainers(
				true,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromClassUnScoped ).isNotNull();
		assertThat( fromClassUnScoped.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );

		final GeneratorAnnotation fromMemberUnScoped = idMember.fromSelfAndContainers(
				true,
				modelsContext,
				(classDetails) -> classDetails.getDirectAnnotationUsage( GeneratorAnnotation.class )
		);
		assertThat( fromMemberUnScoped ).isNotNull();
		assertThat( fromMemberUnScoped.value() ).isEqualTo( GeneratorAnnotation.Source.PACKAGE );
	}
}
