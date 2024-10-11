/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleSerializationTests {
	protected SourceModelBuildingContext createModelContext(Class<?>... classes) {
		return SourceModelTestHelper.createBuildingContext( classes );
	}

	@Test
	void serializeSimpleClass() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClass.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().findClassDetails( SimpleClass.class.getName() );
		assertThat( classDetails ).isNotNull();

		final SourceModelBuildingContext clone = SerializationHelper.clone( buildingContext );
		assertThat( buildingContext ).isNotSameAs( clone );

		final ClassDetails cloneCassDetails = clone.getClassDetailsRegistry().findClassDetails( SimpleClass.class.getName() );
		assertThat( cloneCassDetails ).isNotNull();

		assertThat( classDetails ).isNotSameAs( cloneCassDetails );
		assertThat( buildingContext.getClassDetailsRegistry() ).isNotSameAs( clone.getClassDetailsRegistry() );
		assertThat( buildingContext.getAnnotationDescriptorRegistry() ).isNotSameAs( clone.getAnnotationDescriptorRegistry() );
	}

	@Test
	void serializeSimpleClassWithMembers() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClassWithMembers.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().findClassDetails( SimpleClassWithMembers.class.getName() );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getFields() ).hasSize( 1 );
		assertThat( classDetails.getMethods() ).hasSize( 3 );

		final SourceModelBuildingContext clone = SerializationHelper.clone( buildingContext );
		final ClassDetails cloneCassDetails = clone.getClassDetailsRegistry().findClassDetails( SimpleClassWithMembers.class.getName() );
		assertThat( cloneCassDetails ).isNotNull();
		assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		assertThat( cloneCassDetails.getMethods() ).hasSize( 3 );

		assertThat( buildingContext ).isNotSameAs( clone );
		assertThat( classDetails ).isNotSameAs( cloneCassDetails );
		assertThat( buildingContext.getClassDetailsRegistry() ).isNotSameAs( clone.getClassDetailsRegistry() );
		assertThat( buildingContext.getAnnotationDescriptorRegistry() ).isNotSameAs( clone.getAnnotationDescriptorRegistry() );
	}

	@Test
	void serializeSimpleClassWithAnnotations() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClassWithAnnotations.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().findClassDetails( SimpleClassWithAnnotations.class.getName() );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( classDetails.getFields() ).hasSize( 1 );
		assertThat( classDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( classDetails.getMethods() ).hasSize( 1 );
		assertThat( classDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );

		final SourceModelBuildingContext clone = SerializationHelper.clone( buildingContext );
		final ClassDetails cloneCassDetails = clone.getClassDetailsRegistry().findClassDetails( SimpleClassWithAnnotations.class.getName() );
		assertThat( cloneCassDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		assertThat( cloneCassDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( cloneCassDetails.getMethods() ).hasSize( 1 );
		assertThat( cloneCassDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );

		assertThat( buildingContext ).isNotSameAs( clone );
		assertThat( classDetails ).isNotSameAs( cloneCassDetails );
		assertThat( buildingContext.getClassDetailsRegistry() ).isNotSameAs( clone.getClassDetailsRegistry() );
		assertThat( buildingContext.getAnnotationDescriptorRegistry() ).isNotSameAs( clone.getAnnotationDescriptorRegistry() );
	}

	public static class SimpleClass {
	}

	public static class SimpleClassWithMembers {
		public int anInt;

		public int getAnInt() {
			return anInt;
		}

		public void setAnInt(int anInt) {
			this.anInt = anInt;
		}

		public void doStuff() {
		}
	}

	@Target({ ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	public @interface AnAnnotation {
	}

	@AnAnnotation
	public static class SimpleClassWithAnnotations {
		@AnAnnotation
		private int anInt;

		@AnAnnotation
		public int getAnInt() {
			return anInt;
		}

	}
}
