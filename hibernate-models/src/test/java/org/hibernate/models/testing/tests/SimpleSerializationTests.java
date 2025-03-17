/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.models.serial.spi.StorableContext;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;
import org.hibernate.models.testing.util.SerializationHelper;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.testing.TestHelper.createModelContext;

public class SimpleSerializationTests {
	@Test
	void serializeSimpleClass() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClass.class );
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().findClassDetails( SimpleClass.class.getName() );
		Assertions.assertThat( classDetails ).isNotNull();

		final StorableContext serialContext = buildingContext.toStorableForm();
		final StorableContext clonedSerialContext = SerializationHelper.clone( serialContext );
		Assertions.assertThat( serialContext ).isNotSameAs( clonedSerialContext );

		final SourceModelContext restored = clonedSerialContext.fromStorableForm( SIMPLE_CLASS_LOADING );
		Assertions.assertThat( buildingContext.getClassDetailsRegistry() ).isNotSameAs( restored.getClassDetailsRegistry() );
		Assertions.assertThat( buildingContext.getAnnotationDescriptorRegistry() ).isNotSameAs( restored.getAnnotationDescriptorRegistry() );

		final ClassDetails restoredClassDetails = restored.getClassDetailsRegistry().findClassDetails( SimpleClass.class.getName() );
		Assertions.assertThat( restoredClassDetails ).isNotNull();
		Assertions.assertThat( classDetails ).isNotSameAs( restoredClassDetails );
	}

	@Test
	void serializeSimpleClassWithMembers() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClassWithMembers.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().findClassDetails( SimpleClassWithMembers.class.getName() );
		Assertions.assertThat( classDetails ).isNotNull();
		Assertions.assertThat( classDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getMethods() ).hasSize( 3 );

		final StorableContext serialContext = buildingContext.toStorableForm();
		final StorableContext clonedSerialContext = SerializationHelper.clone( serialContext );
		Assertions.assertThat( serialContext ).isNotSameAs( clonedSerialContext );

		final SourceModelContext restored = clonedSerialContext.fromStorableForm( SIMPLE_CLASS_LOADING );
		Assertions.assertThat( buildingContext ).isNotSameAs( restored );
		Assertions.assertThat( buildingContext.getClassDetailsRegistry() ).isNotSameAs( restored.getClassDetailsRegistry() );
		Assertions.assertThat( buildingContext.getAnnotationDescriptorRegistry() ).isNotSameAs( restored.getAnnotationDescriptorRegistry() );

		final ClassDetails cloneCassDetails = restored.getClassDetailsRegistry().findClassDetails( SimpleClassWithMembers.class.getName() );
		Assertions.assertThat( cloneCassDetails ).isNotNull();
		Assertions.assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getMethods() ).hasSize( 3 );

		Assertions.assertThat( classDetails ).isNotSameAs( cloneCassDetails );
	}

	@Test
	void serializeSimpleClassWithAnnotations() {
		final SourceModelBuildingContext buildingContext = createModelContext( SimpleClassWithAnnotations.class );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().findClassDetails( SimpleClassWithAnnotations.class.getName() );
		Assertions.assertThat( classDetails ).isNotNull();
		Assertions.assertThat( classDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getMethods() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );

		final StorableContext serialContext = buildingContext.toStorableForm();
		final StorableContext clonedSerialContext = SerializationHelper.clone( serialContext );
		Assertions.assertThat( serialContext ).isNotSameAs( clonedSerialContext );

		final SourceModelContext restored = clonedSerialContext.fromStorableForm( SIMPLE_CLASS_LOADING );
		Assertions.assertThat( buildingContext ).isNotSameAs( restored );
		Assertions.assertThat( buildingContext.getClassDetailsRegistry() ).isNotSameAs( restored.getClassDetailsRegistry() );
		Assertions.assertThat( buildingContext.getAnnotationDescriptorRegistry() ).isNotSameAs( restored.getAnnotationDescriptorRegistry() );

		final ClassDetails cloneCassDetails = restored.getClassDetailsRegistry().findClassDetails( SimpleClassWithAnnotations.class.getName() );
		Assertions.assertThat( classDetails ).isNotSameAs( cloneCassDetails );
		Assertions.assertThat( cloneCassDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getMethods() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
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
