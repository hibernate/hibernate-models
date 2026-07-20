/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.serialization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.models.serial.spi.ModelReference;
import org.hibernate.models.serial.spi.ModelsArchive;
import org.hibernate.models.serial.spi.ModelsArchiveWriter;
import org.hibernate.models.serial.spi.ModelsArchives;
import org.hibernate.models.serial.spi.RestoredModels;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.testing.util.SerializationHelper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.testing.TestHelper.createModelContext;

public class SimpleSerializationTests {
	@Test
	void serializeSimpleClass() {
		final ClassDetails classDetails = createModelContext( SimpleClass.class )
				.getClassDetailsRegistry()
				.findClassDetails( SimpleClass.class.getName() );
		assertThat( classDetails ).isNotNull();

		final RestoredClass restoredClass = roundTrip( classDetails );
		assertThat( restoredClass.archive() ).isNotNull();
		assertThat( restoredClass.classDetails() ).isNotNull();
		assertThat( classDetails ).isNotSameAs( restoredClass.classDetails() );
	}

	@Test
	void serializeSimpleClassWithMembers() {
		final ClassDetails classDetails = createModelContext( SimpleClassWithMembers.class )
				.getClassDetailsRegistry()
				.findClassDetails( SimpleClassWithMembers.class.getName() );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getFields() ).hasSize( 1 );
		assertThat( classDetails.getMethods() ).hasSize( 3 );

		final ClassDetails cloneCassDetails = roundTrip( classDetails ).classDetails();
		assertThat( cloneCassDetails ).isNotNull();
		assertThat( classDetails ).isNotSameAs( cloneCassDetails );
		assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		assertThat( cloneCassDetails.getMethods() ).hasSize( 3 );
	}

	@Test
	void serializeSimpleClassWithAnnotations() {
		final ClassDetails classDetails = createModelContext( SimpleClassWithAnnotations.class )
				.getClassDetailsRegistry()
				.findClassDetails( SimpleClassWithAnnotations.class.getName() );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( classDetails.getFields() ).hasSize( 1 );
		assertThat( classDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( classDetails.getMethods() ).hasSize( 1 );
		assertThat( classDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );

		final ClassDetails cloneCassDetails = roundTrip( classDetails ).classDetails();
		assertThat( classDetails ).isNotSameAs( cloneCassDetails );
		assertThat( cloneCassDetails.getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( cloneCassDetails.getFields() ).hasSize( 1 );
		assertThat( cloneCassDetails.getFields().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );
		assertThat( cloneCassDetails.getMethods() ).hasSize( 1 );
		assertThat( cloneCassDetails.getMethods().iterator().next().getDirectAnnotationUsages() ).hasSize( 1 );

	}

	private RestoredClass roundTrip(ClassDetails classDetails) {
		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference reference = writer.reference( classDetails );
		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );
		final RestoredModels restoredModels = archive.restore( SIMPLE_CLASS_LOADING, null );
		return new RestoredClass( archive, (ClassDetails) restoredModels.resolve( reference ) );
	}

	private record RestoredClass(ModelsArchive archive, ClassDetails classDetails) {
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
