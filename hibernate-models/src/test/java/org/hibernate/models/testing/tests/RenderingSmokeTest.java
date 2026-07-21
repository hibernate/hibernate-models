/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.testing.annotations.EverythingBagel;
import org.hibernate.models.testing.annotations.Nested;
import org.hibernate.models.testing.annotations.Status;
import org.hibernate.models.testing.domain.SimpleEntity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class RenderingSmokeTest {
	@Test
	void testCompleteAndClassOnlyRendering() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().resolveClassDetails( SimpleEntity.class.getName() );

		final String complete = classDetails.render( modelsContext );
		assertThat( complete )
				.contains( "class " + SimpleEntity.class.getName() )
				.contains( "java.lang.Integer id" )
				.contains( "getId (GETTER)" );

		final String classOnly = classDetails.render( modelsContext, ClassDetails.RenderMode.CLASS_ONLY );
		assertThat( classOnly )
				.contains( "@jakarta.persistence.Entity" )
				.contains( "class " + SimpleEntity.class.getName() )
				.doesNotContain( "java.lang.Integer id" )
				.doesNotContain( "getId (GETTER)" );
	}

	@Test
	void testIndividualMemberRendering() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class, SimpleRecord.class );
		final ClassDetails entity = modelsContext.getClassDetailsRegistry().resolveClassDetails( SimpleEntity.class.getName() );

		final FieldDetails field = entity.findFieldByName( "id" );
		assertThat( field.render( modelsContext ) )
				.contains( "@jakarta.persistence.Id" )
				.contains( "java.lang.Integer id" );

		final MethodDetails method = entity.getMethods()
				.stream()
				.filter( candidate -> candidate.getName().equals( "getId" ) )
				.findFirst()
				.orElseThrow();
		assertThat( method.render( modelsContext ) ).contains( "getId (GETTER)" );

		final ClassDetails record = modelsContext.getClassDetailsRegistry().resolveClassDetails( SimpleRecord.class.getName() );
		final RecordComponentDetails component = record.getRecordComponents().get( 0 );
		assertThat( component.render( modelsContext ) ).contains( "java.lang.String value" );
	}

	@Test
	void testContainedAnnotationRendering() {
		final ModelsContext modelsContext = createModelContext( SimpleClass.class );
		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );

		assertThat( classDetails.render( modelsContext, ClassDetails.RenderMode.CLASS_ONLY ) )
				.contains( "theString = \"hello\"" )
				.contains( "theLong = 4L" )
				.contains( "theFloat = 5.1F" )
				.contains( "theClass = " + SimpleEntity.class.getName() )
				.contains( "theNested = @" + Nested.class.getName() )
				.contains( "theNesteds" )
				.contains( "theStrings = {", "\"a\"", "\"b\"", "\"c\"" );
	}


	@EverythingBagel(
			theString = "hello",
			theEnum = Status.ACTIVE,
			theBoolean = true,
			theByte = 1,
			theShort = 2,
			theInteger = 3,
			theLong = 4L,
			theFloat = 5.1F,
			theDouble = 6.2,
			theClass = SimpleEntity.class,
			theNested = @Nested(),
			theNesteds = {@Nested(), @Nested()},
			theStrings = {"a", "b", "c"}
	)
	public static class SimpleClass {
	}

	public record SimpleRecord(String value) {
	}
}
