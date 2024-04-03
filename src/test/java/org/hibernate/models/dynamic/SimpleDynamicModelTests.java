/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.dynamic;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.internal.dynamic.DynamicFieldDetails;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MutableAnnotationUsage;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class SimpleDynamicModelTests {
	@Test
	void testSimpleBasics() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext( (Index) null );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails integerClassDetails = classDetailsRegistry.getClassDetails( Integer.class.getName() );
		final ClassTypeDetailsImpl integerTypeDetails = new ClassTypeDetailsImpl( integerClassDetails, TypeDetails.Kind.CLASS );

		final ClassDetails stringClassDetails = classDetailsRegistry.getClassDetails( String.class.getName() );
		final ClassTypeDetailsImpl stringTypeDetails = new ClassTypeDetailsImpl( stringClassDetails, TypeDetails.Kind.CLASS );

		final DynamicClassDetails entityDetails = (DynamicClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheEntity",
				name -> new DynamicClassDetails( name, buildingContext )
		);
		final MutableAnnotationUsage<Entity> created = entityDetails.applyAnnotationUsage(
				JpaAnnotations.ENTITY,
				buildingContext
		);
		final MutableAnnotationUsage<Entity> preExisting = entityDetails.applyAnnotationUsage(
				JpaAnnotations.ENTITY,
				buildingContext
		);
		assertThat( created ).isSameAs( preExisting );

		entityDetails.applyAttribute(
				"id",
				integerTypeDetails,
				false,
				false,
				fieldDetails -> {
					final MutableAnnotationUsage<Id> first = fieldDetails.applyAnnotationUsage(
							JpaAnnotations.ID,
							buildingContext
					);
					final MutableAnnotationUsage<Id> second = fieldDetails.applyAnnotationUsage(
							JpaAnnotations.ID,
							buildingContext
					);
					assertThat( first ).isSameAs( second );
				},
				buildingContext
		);

		entityDetails.applyAttribute(
				"name",
				stringTypeDetails,
				false,
				false,
				null,
				buildingContext
		);

		assertThat( entityDetails.getFields() ).hasSize( 2 );
		assertThat( entityDetails.getFields().get( 0 ).getName() ).isEqualTo( "id" );
		assertThat( entityDetails.getFields().get( 0 ).hasAnnotationUsage( Id.class ) ).isTrue();
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
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext( (Index) null );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails integerClassDetails = classDetailsRegistry.getClassDetails( Integer.class.getName() );
		final ClassTypeDetailsImpl integerTypeDetails = new ClassTypeDetailsImpl( integerClassDetails, TypeDetails.Kind.CLASS );

		final ClassDetails stringClassDetails = classDetailsRegistry.getClassDetails( String.class.getName() );
		final ClassTypeDetailsImpl stringTypeDetails = new ClassTypeDetailsImpl( stringClassDetails, TypeDetails.Kind.CLASS );

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// EMBEDDABLE - TheName

		final DynamicClassDetails nameEmbeddableDetails = (DynamicClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheName",
				name -> {
					final DynamicClassDetails classDetails = new DynamicClassDetails( name, buildingContext );
					final MutableAnnotationUsage<Embeddable> embeddableUsage = classDetails.applyAnnotationUsage( JpaAnnotations.EMBEDDABLE, buildingContext );
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
				null,
				buildingContext
		);

		final DynamicFieldDetails lastNameMember = nameEmbeddableDetails.applyAttribute(
				"last",
				stringClassDetails,
				false,
				false,
				null,
				buildingContext
		);


		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// ENTITY - TheEntity

		final DynamicClassDetails entityDetails = (DynamicClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheEntity",
				name -> {
					final DynamicClassDetails classDetails = new DynamicClassDetails( name, buildingContext );
					final MutableAnnotationUsage<Entity> entityUsage = classDetails.applyAnnotationUsage( JpaAnnotations.ENTITY, buildingContext );
					assertThat( entityUsage ).isNotNull();
					return classDetails;
				}
		);

		final DynamicFieldDetails idMember = entityDetails.applyAttribute(
				"id",
				integerTypeDetails,
				false,
				false,
				(fieldDetails) -> {
					final MutableAnnotationUsage<Id> idUsage = fieldDetails.applyAnnotationUsage( JpaAnnotations.ID, buildingContext );
					assertThat( idUsage ).isNotNull();
				},
				buildingContext
		);

		final DynamicFieldDetails nameMember = entityDetails.applyAttribute(
				"name",
				new ClassTypeDetailsImpl( nameEmbeddableDetails, TypeDetails.Kind.CLASS ),
				false,
				false,
				(fieldDetails) -> {
					final MutableAnnotationUsage<Embedded> embeddedUsage = fieldDetails.applyAnnotationUsage( JpaAnnotations.EMBEDDED, buildingContext );
					assertThat( embeddedUsage ).isNotNull();
				},
				buildingContext
		);


		// ASSERTIONS

		assertThat( entityDetails.getFields() ).containsExactly( idMember, nameMember );
		checkPersistability( idMember );
		assertThat( idMember.hasAnnotationUsage( Id.class ) ).isTrue();
		checkPersistability( nameMember );
		assertThat( nameMember.hasAnnotationUsage( Embedded.class ) ).isTrue();

		assertThat( nameEmbeddableDetails.getFields() ).containsExactly( firstNameMember, lastNameMember );
		checkPersistability( firstNameMember );
		checkPersistability( lastNameMember );
	}
}
