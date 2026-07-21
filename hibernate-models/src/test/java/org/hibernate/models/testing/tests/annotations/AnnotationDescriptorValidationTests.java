/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.InvalidAnnotationUsageException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.models.testing.TestHelper.createModelContext;

public class AnnotationDescriptorValidationTests {
	@Test
	void validatesCompleteUsageRecursively() {
		final ModelsContext modelsContext = createModelContext( Annotated.class );
		final AnnotationDescriptor<Parent> descriptor = modelsContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( Parent.class );
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( Annotated.class.getName() );

		descriptor.validateUsage( classDetails.getDirectAnnotationUsage( Parent.class ), modelsContext );
	}

	@Test
	void rejectsNullAttributeValue() {
		final ModelsContext modelsContext = createModelContext( Annotated.class );
		final AnnotationDescriptor<Child> descriptor = modelsContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( Child.class );
		final Map<String, Object> values = new HashMap<>();
		values.put( "value", null );
		final Child usage = descriptor.createUsage( values, modelsContext );

		assertThatThrownBy( () -> descriptor.validateUsage( usage, modelsContext ) )
				.isInstanceOfSatisfying( InvalidAnnotationUsageException.class, (exception) -> {
					assertThat( exception.getAnnotationType() ).isEqualTo( Child.class );
					assertThat( exception.getAttributePath() ).isEqualTo( "@" + Child.class.getName() + ".value" );
				} )
				.hasMessageContaining( "must not be null" );
	}

	@Test
	void reportsNestedAttributePath() {
		final ModelsContext modelsContext = createModelContext( Annotated.class );
		final AnnotationDescriptor<Child> childDescriptor = modelsContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( Child.class );
		final AnnotationDescriptor<Parent> parentDescriptor = modelsContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( Parent.class );

		final Map<String, Object> childValues = new HashMap<>();
		childValues.put( "value", null );
		final Child child = childDescriptor.createUsage( childValues, modelsContext );
		final Parent parent = parentDescriptor.createUsage(
				Map.of( "child", child, "children", new Child[] { child } ),
				modelsContext
		);

		assertThatThrownBy( () -> parentDescriptor.validateUsage( parent, modelsContext ) )
				.isInstanceOf( InvalidAnnotationUsageException.class )
				.hasMessageContaining( "@" + Parent.class.getName() + ".child.value" );
	}

	@Test
	void rejectsWrongAttributeType() {
		final ModelsContext modelsContext = createModelContext( Annotated.class );
		final AnnotationDescriptor<Child> descriptor = modelsContext
				.getAnnotationDescriptorRegistry()
				.getDescriptor( Child.class );
		final Child usage = descriptor.createUsage( Map.of( "value", 1 ), modelsContext );

		assertThatThrownBy( () -> descriptor.validateUsage( usage, modelsContext ) )
				.isInstanceOf( InvalidAnnotationUsageException.class )
				.hasMessageContaining( "@" + Child.class.getName() + ".value" );
	}

	@Target({})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Child {
		String value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Parent {
		Child child();

		Child[] children();
	}

	@Parent(child = @Child("ok"), children = {@Child("one"), @Child("two")})
	public static class Annotated {
	}
}
