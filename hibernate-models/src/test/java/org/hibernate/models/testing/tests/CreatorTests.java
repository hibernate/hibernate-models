/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.lang.reflect.Modifier;
import java.util.EnumSet;

import org.hibernate.models.CompleteAnnotationDescriptor;
import org.hibernate.models.Creator;
import org.hibernate.models.dynamic.DynamicClassDetails;
import org.hibernate.models.dynamic.DynamicFieldDetails;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.jdk.JdkClassDetails;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget.Kind;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableAnnotationDescriptor;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.StandardAnnotationDescriptor;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.testing.annotations.CustomAnnotation;
import org.hibernate.models.testing.annotations.CustomAnnotations;
import org.hibernate.models.testing.annotations.CustomMetaAnnotation;
import org.hibernate.models.testing.orm.EntityAnnotation;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

class CreatorTests {
	@Test
	void createCompleteAnnotationDescriptor() {
		final ModelsContext modelsContext = createModelContext();
		final EnumSet<Kind> targets = EnumSet.of( Kind.CLASS );

		final MutableAnnotationDescriptor<Entity, EntityAnnotation> descriptor = Creator.createCompleteAnnotationDescriptor(
				Entity.class,
				EntityAnnotation.class,
				targets,
				true
		);

		assertThat( descriptor ).isInstanceOf( CompleteAnnotationDescriptor.class );
		assertThat( descriptor.getAnnotationType() ).isEqualTo( Entity.class );
		assertThat( descriptor.getMutableAnnotationType() ).isEqualTo( EntityAnnotation.class );
		assertThat( descriptor.getAllowableTargets() ).isEqualTo( targets );
		assertThat( descriptor.isInherited() ).isTrue();
		assertThat( descriptor.getRepeatableContainer() ).isNull();
		assertThat( descriptor.createUsage( modelsContext ) ).isInstanceOf( EntityAnnotation.class );
	}

	@Test
	void createCompleteAnnotationDescriptorWithRepeatableContainer() {
		final ModelsContext modelsContext = createModelContext();
		final AnnotationDescriptor<Entity> container = Creator.createCompleteAnnotationDescriptor(
				Entity.class,
				EntityAnnotation.class,
				EnumSet.of( Kind.CLASS ),
				false
		);

		final AnnotationDescriptor<Entity> descriptor = Creator.createCompleteAnnotationDescriptor(
				Entity.class,
				EntityAnnotation.class,
				EnumSet.of( Kind.CLASS ),
				false,
				container
		);

		assertThat( descriptor.getRepeatableContainer() ).isSameAs( container );
		assertThat( descriptor.createUsage( modelsContext ) ).isInstanceOf( EntityAnnotation.class );
	}

	@Test
	void createAnnotationDescriptor() {
		final ModelsContext modelsContext = createModelContext();

		final AnnotationDescriptor<CustomAnnotation> descriptor = Creator.createAnnotationDescriptor(
				CustomAnnotation.class,
				modelsContext
		);

		assertThat( descriptor ).isInstanceOf( StandardAnnotationDescriptor.class );
		assertThat( descriptor.getAnnotationType() ).isEqualTo( CustomAnnotation.class );
		assertThat( descriptor.getAllowableTargets() ).containsExactly( Kind.CLASS );
		assertThat( descriptor.isInherited() ).isTrue();
		assertThat( descriptor.hasDirectAnnotationUsage( CustomMetaAnnotation.class ) ).isTrue();
	}

	@Test
	void createAnnotationDescriptorWithRepeatableContainer() {
		final ModelsContext modelsContext = createModelContext();
		final AnnotationDescriptor<CustomAnnotations> container = Creator.createAnnotationDescriptor(
				CustomAnnotations.class,
				modelsContext
		);

		final AnnotationDescriptor<CustomAnnotation> descriptor = Creator.createAnnotationDescriptor(
				CustomAnnotation.class,
				container,
				modelsContext
		);

		assertThat( descriptor ).isInstanceOf( StandardAnnotationDescriptor.class );
		assertThat( descriptor.getRepeatableContainer() ).isSameAs( container );
	}

	@Test
	void createDynamicClassAndMembers() {
		final ModelsContext modelsContext = createModelContext();
		final MutableClassDetails classDetails = Creator.createDynamicClassDetails( "DynamicEntity", modelsContext );
		final TypeDetails stringType = new ClassTypeDetailsImpl(
				modelsContext.getClassDetailsRegistry().resolveClassDetails( String.class.getName() ),
				TypeDetails.Kind.CLASS
		);

		assertThat( classDetails ).isInstanceOf( DynamicClassDetails.class );
		assertThat( classDetails.getName() ).isEqualTo( "DynamicEntity" );
		assertThat( classDetails.getClassName() ).isNull();

		final int modifiers = Modifier.PUBLIC;
		final MutableMemberDetails explicitMember = Creator.createDynamicMemberDetails(
				"explicitMember",
				stringType,
				classDetails,
				modifiers,
				true,
				false,
				modelsContext
		);
		assertDynamicMember( explicitMember, "explicitMember", stringType, classDetails, modifiers, true, false );

		final MutableMemberDetails defaultMember = Creator.createDynamicMemberDetails(
				"defaultMember",
				stringType,
				classDetails,
				false,
				true,
				modelsContext
		);
		assertDynamicMember(
				defaultMember,
				"defaultMember",
				stringType,
				classDetails,
				Creator.DYNAMIC_ATTRIBUTE_MODIFIERS,
				false,
				true
		);
	}

	@Test
	void createJdkClassDetails() {
		final ModelsContext modelsContext = createModelContext();

		final MutableClassDetails classDetails = Creator.createJdkClassDetails( Sample.class, modelsContext );
		assertJdkClassDetails( classDetails, Sample.class.getName(), Sample.class.getName() );

		final MutableClassDetails namedClassDetails = Creator.createJdkClassDetails(
				"SampleEntity",
				Sample.class,
				modelsContext
		);
		assertJdkClassDetails( namedClassDetails, "SampleEntity", Sample.class.getName() );
	}

	private static void assertDynamicMember(
			MutableMemberDetails member,
			String name,
			TypeDetails type,
			ClassDetails declaringType,
			int modifiers,
			boolean array,
			boolean plural) {
		assertThat( member ).isInstanceOf( DynamicFieldDetails.class );
		assertThat( member.getName() ).isEqualTo( name );
		assertThat( member.getType() ).isSameAs( type );
		assertThat( member.getDeclaringType() ).isSameAs( declaringType );
		assertThat( member.getModifiers() ).isEqualTo( modifiers );
		assertThat( member.isArray() ).isEqualTo( array );
		assertThat( member.isPlural() ).isEqualTo( plural );
	}

	private static void assertJdkClassDetails(MutableClassDetails classDetails, String name, String className) {
		assertThat( classDetails ).isInstanceOf( JdkClassDetails.class );
		assertThat( classDetails.getName() ).isEqualTo( name );
		assertThat( classDetails.getClassName() ).isEqualTo( className );
		assertThat( classDetails.toJavaClass() ).isEqualTo( Sample.class );
	}

	private static class Sample {
	}
}
