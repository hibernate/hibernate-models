/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.dynamic;

import java.lang.reflect.Modifier;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.internal.dynamic.DynamicFieldDetails;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

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

		final MutableClassDetails entityDetails = (MutableClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheEntity",
				name -> new DynamicClassDetails( name, buildingContext )
		);
		entityDetails.addAnnotationUsage( JpaAnnotations.ENTITY.createUsage( entityDetails, buildingContext ) );


		final DynamicFieldDetails idFieldDetails = new DynamicFieldDetails(
				"id",
				integerTypeDetails,
				entityDetails,
				Modifier.fieldModifiers(),
				false,
				false,
				buildingContext
		);
		entityDetails.addField( idFieldDetails );

		final DynamicFieldDetails nameFieldDetails = new DynamicFieldDetails(
				"name",
				stringTypeDetails,
				entityDetails,
				Modifier.fieldModifiers(),
				false,
				false,
				buildingContext
		);
		entityDetails.addField( nameFieldDetails );
	}

	@Test
	void testSimpleEmbedded() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext( (Index) null );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails integerClassDetails = classDetailsRegistry.getClassDetails( Integer.class.getName() );
		final ClassTypeDetailsImpl integerTypeDetails = new ClassTypeDetailsImpl( integerClassDetails, TypeDetails.Kind.CLASS );

		final ClassDetails stringClassDetails = classDetailsRegistry.getClassDetails( String.class.getName() );
		final ClassTypeDetailsImpl stringTypeDetails = new ClassTypeDetailsImpl( stringClassDetails, TypeDetails.Kind.CLASS );

		final MutableClassDetails entityDetails = (MutableClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheEntity",
				name -> new DynamicClassDetails( name, buildingContext )
		);
		entityDetails.addAnnotationUsage( JpaAnnotations.ENTITY.createUsage( entityDetails, buildingContext ) );

		final MutableClassDetails nameEmbeddableDetails = (MutableClassDetails) classDetailsRegistry.resolveClassDetails(
				"TheName",
				name -> new DynamicClassDetails( name, buildingContext )
		);
		nameEmbeddableDetails.addAnnotationUsage( JpaAnnotations.EMBEDDABLE.createUsage( entityDetails, buildingContext ) );
		final ClassTypeDetailsImpl nameEmbeddableTypeDetails = new ClassTypeDetailsImpl( nameEmbeddableDetails, TypeDetails.Kind.CLASS );

		final DynamicFieldDetails firstFieldDetails = new DynamicFieldDetails(
				"first",
				stringTypeDetails,
				nameEmbeddableDetails,
				Modifier.fieldModifiers(),
				false,
				false,
				buildingContext
		);
		nameEmbeddableDetails.addField( firstFieldDetails );

		final DynamicFieldDetails lastFieldDetails = new DynamicFieldDetails(
				"last",
				stringTypeDetails,
				nameEmbeddableDetails,
				Modifier.fieldModifiers(),
				false,
				false,
				buildingContext
		);
		nameEmbeddableDetails.addField( lastFieldDetails );


		final DynamicFieldDetails nameFieldDetails = new DynamicFieldDetails(
				"name",
				nameEmbeddableTypeDetails,
				entityDetails,
				Modifier.fieldModifiers(),
				false,
				false,
				buildingContext
		);
		entityDetails.addField( nameFieldDetails );
	}
}
