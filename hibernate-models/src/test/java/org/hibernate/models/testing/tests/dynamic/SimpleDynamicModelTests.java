/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.dynamic;

import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.internal.dynamic.DynamicFieldDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.testing.TestHelper;
import org.hibernate.models.testing.orm.JpaAnnotations;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class SimpleDynamicModelTests {
	@Test
	void testSimpleBasics() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails integerClassDetails = classDetailsRegistry.getClassDetails( Integer.class.getName() );
		final ClassTypeDetailsImpl integerTypeDetails = new ClassTypeDetailsImpl( integerClassDetails, TypeDetails.Kind.CLASS );

		final ClassDetails stringClassDetails = classDetailsRegistry.getClassDetails( String.class.getName() );
		final ClassTypeDetailsImpl stringTypeDetails = new ClassTypeDetailsImpl( stringClassDetails, TypeDetails.Kind.CLASS );

		classDetailsRegistry.as( MutableClassDetailsRegistry.class )
				.addClassDetails( "TheEntity", new DynamicClassDetails( "TheEntity", modelsContext ) );

		final DynamicClassDetails entityDetails = (DynamicClassDetails) classDetailsRegistry.resolveClassDetails( "TheEntity" );
		final Entity created = entityDetails.applyAnnotationUsage(
				JpaAnnotations.ENTITY,
				modelsContext
		);
		final Entity preExisting = entityDetails.applyAnnotationUsage(
				JpaAnnotations.ENTITY,
				modelsContext
		);
		assertThat( created ).isSameAs( preExisting );

		final DynamicFieldDetails idMember = entityDetails.applyAttribute(
				"id",
				integerTypeDetails,
				false,
				false,
				modelsContext
		);
		final Id first = idMember.applyAnnotationUsage(
				JpaAnnotations.ID,
				modelsContext
		);
		final Id second = idMember.applyAnnotationUsage(
				JpaAnnotations.ID,
				modelsContext
		);
		assertThat( first ).isSameAs( second );

		entityDetails.applyAttribute(
				"name",
				stringTypeDetails,
				false,
				false,
				modelsContext
		);

		assertThat( entityDetails.getFields() ).hasSize( 2 );
		assertThat( entityDetails.getFields().get( 0 ).getName() ).isEqualTo( "id" );
		assertThat( entityDetails.getFields().get( 0 ).hasDirectAnnotationUsage( Id.class ) ).isTrue();
		checkPersistability( entityDetails.getFields().get( 0 ) );
		checkPersistability( entityDetails.getFields().get( 1 ) );
	}


	@Test
	void testResolveClassDetails() {
		final ModelsContext modelsContext = createModelContext();
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails integerClassDetails = classDetailsRegistry.getClassDetails( Integer.class.getName() );
		final ClassTypeDetailsImpl integerTypeDetails = new ClassTypeDetailsImpl( integerClassDetails, TypeDetails.Kind.CLASS );

		final ClassDetails stringClassDetails = classDetailsRegistry.getClassDetails( String.class.getName() );
		final ClassTypeDetailsImpl stringTypeDetails = new ClassTypeDetailsImpl( stringClassDetails, TypeDetails.Kind.CLASS );

		classDetailsRegistry.as( MutableClassDetailsRegistry.class )
				.resolveClassDetails( "TheEntity", (name) -> new DynamicClassDetails( name, modelsContext ) );

		final DynamicClassDetails entityDetails = (DynamicClassDetails) classDetailsRegistry.resolveClassDetails( "TheEntity" );
		final Entity created = entityDetails.applyAnnotationUsage(
				JpaAnnotations.ENTITY,
				modelsContext
		);
		final Entity preExisting = entityDetails.applyAnnotationUsage(
				JpaAnnotations.ENTITY,
				modelsContext
		);
		assertThat( created ).isSameAs( preExisting );

		final DynamicFieldDetails idMember = entityDetails.applyAttribute(
				"id",
				integerTypeDetails,
				false,
				false,
				modelsContext
		);
		final Id first = idMember.applyAnnotationUsage(
				JpaAnnotations.ID,
				modelsContext
		);
		final Id second = idMember.applyAnnotationUsage(
				JpaAnnotations.ID,
				modelsContext
		);
		assertThat( first ).isSameAs( second );

		entityDetails.applyAttribute(
				"name",
				stringTypeDetails,
				false,
				false,
				modelsContext
		);

		assertThat( entityDetails.getFields() ).hasSize( 2 );
		assertThat( entityDetails.getFields().get( 0 ).getName() ).isEqualTo( "id" );
		assertThat( entityDetails.getFields().get( 0 ).hasDirectAnnotationUsage( Id.class ) ).isTrue();
		checkPersistability( entityDetails.getFields().get( 0 ) );
		checkPersistability( entityDetails.getFields().get( 1 ) );
	}

	private void checkPersistability(FieldDetails fieldDetails) {
		assertThat( fieldDetails.isPersistable() ).isTrue();

		// check the individual flags
		assertThat( ModifierUtils.isAbstract( fieldDetails.getModifiers() ) ).isFalse();
		assertThat( ModifierUtils.isFinal( fieldDetails.getModifiers() ) ).isFalse();
		assertThat( ModifierUtils.isTransient( fieldDetails.getModifiers() ) ).isFalse();
		assertThat( ModifierUtils.isSynthetic( fieldDetails.getModifiers() ) ).isFalse();
	}

	@Test
	void testSimpleEmbedded() {
		final ModelsContext modelsContext = TestHelper.createModelContext();
		final MutableClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry().as( MutableClassDetailsRegistry.class );

		final ClassDetails integerClassDetails = classDetailsRegistry.getClassDetails( Integer.class.getName() );
		final ClassTypeDetailsImpl integerTypeDetails = new ClassTypeDetailsImpl( integerClassDetails, TypeDetails.Kind.CLASS );

		final ClassDetails stringClassDetails = classDetailsRegistry.getClassDetails( String.class.getName() );

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// EMBEDDABLE - TheName

		final DynamicClassDetails nameEmbeddableDetails = (DynamicClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheName",
				name -> {
					final DynamicClassDetails classDetails = new DynamicClassDetails( name, modelsContext );
					final Embeddable embeddableUsage = classDetails.applyAnnotationUsage( JpaAnnotations.EMBEDDABLE, modelsContext );
					assertThat( embeddableUsage ).isNotNull();
					return classDetails;
				}
		);

		// NOTE : here we use the form accepting a ClassDetails for the attribute type
		// rather than the TypeDetails form used elsewhere for code coverage

		final DynamicFieldDetails firstNameMember = nameEmbeddableDetails.applyAttribute(
				"first",
				stringClassDetails,
				false,
				false,
				modelsContext
		);

		final DynamicFieldDetails lastNameMember = nameEmbeddableDetails.applyAttribute(
				"last",
				stringClassDetails,
				false,
				false,
				modelsContext
		);


		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ENTITY - TheEntity

		final DynamicClassDetails entityDetails = (DynamicClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheEntity",
				name -> {
					final DynamicClassDetails classDetails = new DynamicClassDetails( name, modelsContext );
					final Entity entityUsage = classDetails.applyAnnotationUsage( JpaAnnotations.ENTITY, modelsContext );
					assertThat( entityUsage ).isNotNull();
					return classDetails;
				}
		);

		final DynamicFieldDetails idMember = entityDetails.applyAttribute(
				"id",
				integerTypeDetails,
				false,
				false,
				modelsContext
		);
		final Id idUsage = idMember.applyAnnotationUsage( JpaAnnotations.ID, modelsContext );
		assertThat( idUsage ).isNotNull();

		final DynamicFieldDetails nameMember = entityDetails.applyAttribute(
				"name",
				new ClassTypeDetailsImpl( nameEmbeddableDetails, TypeDetails.Kind.CLASS ),
				false,
				false,
				modelsContext
		);
		final Embedded embeddedUsage = nameMember.applyAnnotationUsage( JpaAnnotations.EMBEDDED, modelsContext );
		assertThat( embeddedUsage ).isNotNull();


		// ASSERTIONS

		assertThat( entityDetails.getFields() ).containsExactly( idMember, nameMember );
		checkPersistability( idMember );
		assertThat( idMember.hasDirectAnnotationUsage( Id.class ) ).isTrue();
		checkPersistability( nameMember );
		assertThat( nameMember.hasDirectAnnotationUsage( Embedded.class ) ).isTrue();

		assertThat( nameEmbeddableDetails.getFields() ).containsExactly( firstNameMember, lastNameMember );
		checkPersistability( firstNameMember );
		checkPersistability( lastNameMember );
	}
}
