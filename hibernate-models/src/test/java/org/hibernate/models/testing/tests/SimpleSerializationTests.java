/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.models.serial.spi.ModelReference;
import org.hibernate.models.serial.spi.ModelsArchiveWriter;
import org.hibernate.models.serial.spi.ModelsArchives;
import org.hibernate.models.serial.spi.RestoredModels;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.testing.util.SerializationHelper;

import org.junit.jupiter.api.Test;

import org.assertj.core.api.Assertions;

import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.testing.TestHelper.createModelContext;

public class SimpleSerializationTests {
	@Test
	void serializeSimpleClass() {
		final ClassDetails classDetails = createModelContext( SimpleClass.class )
				.getClassDetailsRegistry()
				.findClassDetails( SimpleClass.class.getName() );
		Assertions.assertThat( classDetails ).isNotNull();

		final ClassDetails restoredClassDetails = roundTrip( classDetails );
		Assertions.assertThat( restoredClassDetails ).isNotNull();
		Assertions.assertThat( classDetails ).isNotSameAs( restoredClassDetails );
	}

	@Test
	void serializeSimpleClassWithMembers() {
		final ClassDetails classDetails = createModelContext( SimpleClassWithMembers.class )
				.getClassDetailsRegistry()
				.findClassDetails( SimpleClassWithMembers.class.getName() );
		Assertions.assertThat( classDetails ).isNotNull();
		Assertions.assertThat( classDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getMethods() ).hasSize( 3 );

		final ClassDetails cloneCassDetails = roundTrip( classDetails );
		Assertions.assertThat( cloneCassDetails ).isNotNull();
		Assertions.assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getMethods() ).hasSize( 3 );

		Assertions.assertThat( classDetails ).isNotSameAs( cloneCassDetails );
	}

	@Test
	void serializeSimpleClassWithAnnotations() {
		final ClassDetails classDetails = createModelContext( SimpleClassWithAnnotations.class )
				.getClassDetailsRegistry()
				.findClassDetails( SimpleClassWithAnnotations.class.getName() );
		Assertions.assertThat( classDetails ).isNotNull();
		Assertions.assertThat( classDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getMethods() ).hasSize( 1 );
		Assertions.assertThat( classDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );

		final ClassDetails cloneCassDetails = roundTrip( classDetails );
		Assertions.assertThat( classDetails ).isNotSameAs( cloneCassDetails );
		Assertions.assertThat( cloneCassDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getMethods() ).hasSize( 1 );
		Assertions.assertThat( cloneCassDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
	}

	private ClassDetails roundTrip(ClassDetails classDetails) {
		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference reference = writer.reference( classDetails );
		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		return (ClassDetails) restoredModels.resolve( reference );
	}

	public static class SimpleClass {
	}

	@SuppressWarnings("unused")
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
