/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.shared.intg.ByteBuddyModelContextFactory.CONTEXT_FACTORY;

/**
 * @author Steve Ebersole
 */
public class AnnotationHandlingTests {
	@Test
	void testSimpleUsages() {
		final SourceModelBuildingContext buildingContext = CONTEXT_FACTORY.createModelContext( null, SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().resolveClassDetails( SimpleClass.class.getName() );
		assertThat( classDetails ).isNotNull();

		final MyOtherAnnotation annotationUsage = classDetails.getDirectAnnotationUsage( MyOtherAnnotation.class );
		assertThat( annotationUsage ).isNotNull();
		assertThat( annotationUsage.theNested().value() ).isEqualTo( "nested" );
		assertThat( annotationUsage.theNesteds() ).hasSize( 1 );
		assertThat( annotationUsage.theNesteds()[0].value() ).isEqualTo( "nesteds[0]" );

	}

	public enum MyEnum {FIRST,SECOND,THIRD}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyAnnotation {
		String value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface MyOtherAnnotation {
		MyEnum theEnum();
		int theInt();
		String theString();
		MyAnnotation theNested();
		MyAnnotation[] theNesteds();
	}

	@MyOtherAnnotation(
			theEnum = MyEnum.SECOND,
			theInt = Integer.MAX_VALUE,
			theString = "Some string",
			theNested = @MyAnnotation( "nested" ),
			theNesteds = @MyAnnotation( "nesteds[0]" )
	)
	public static class SimpleClass {
	}
}
