/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.dynamic;

import java.util.List;

import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.dynamic.DynamicAnnotationUsage;
import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MutableAnnotationUsage;

import org.junit.jupiter.api.Test;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinTable;
import jakarta.persistence.SequenceGenerator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class DynamicAnnotationTests {
	@Test
	void testBasicUsage() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext();
		final DynamicClassDetails dynamicEntity = new DynamicClassDetails( "DynamicEntity", buildingContext );
		final DynamicAnnotationUsage<SequenceGenerator> generatorAnn = new DynamicAnnotationUsage<>(
				JpaAnnotations.SEQUENCE_GENERATOR,
				buildingContext
		);
		assertThat( generatorAnn.getString( "name" ) ).isEqualTo( "" );
		assertThat( generatorAnn.getString( "sequenceName" ) ).isEqualTo( "" );
		assertThat( generatorAnn.getString( "catalog" ) ).isEqualTo( "" );
		assertThat( generatorAnn.getString( "schema" ) ).isEqualTo( "" );
		assertThat( generatorAnn.getInteger( "initialValue" ) ).isEqualTo( 1 );
		assertThat( generatorAnn.getInteger( "allocationSize" ) ).isEqualTo( 50 );

		try {
			generatorAnn.getInteger( "incrementBy" );
			fail( "Expecting UnknownAnnotationAttributeException" );
		}
		catch (UnknownAnnotationAttributeException expected) {
			// ignore
		}

	}

	@Test
	void testJoinTableForeignKeyDefaultValue() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext();
		final DynamicClassDetails dynamicEntity = new DynamicClassDetails( "DynamicEntity", buildingContext );
		final DynamicAnnotationUsage<JoinTable> generatorAnn = new DynamicAnnotationUsage<>(
				JpaAnnotations.JOIN_TABLE,
				buildingContext
		);

		final Object foreignKey = generatorAnn.getAttributeValue( "foreignKey" );

		assertThat( foreignKey ).isInstanceOf( AnnotationUsage.class );

		//noinspection unchecked
		final AnnotationUsage<ForeignKey> foreignKeyAnnotationUsage = (AnnotationUsage<ForeignKey>) foreignKey;

		assertThat( foreignKeyAnnotationUsage.<ConstraintMode>getAttributeValue( "value" ) ).isEqualTo( ConstraintMode.PROVIDER_DEFAULT );

		assertThat( foreignKeyAnnotationUsage.<String>getAttributeValue( "name" ) ).isEqualTo( "" );
		assertThat( foreignKeyAnnotationUsage.<String>getAttributeValue( "options" ) ).isEqualTo( "" );
		assertThat( foreignKeyAnnotationUsage.<String>getAttributeValue( "foreignKeyDefinition" ) ).isEqualTo( "" );
	}

	@Test
	void testDefaultArrayValue() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext();
		final DynamicClassDetails dynamicEntity = new DynamicClassDetails( "DynamicEntity", buildingContext );
		final DynamicAnnotationUsage<JoinTable> generatorAnn = new DynamicAnnotationUsage<>(
				JpaAnnotations.JOIN_TABLE,
				buildingContext
		);

		final Object joinColumns = generatorAnn.getAttributeValue( "joinColumns" );
		assertThat( joinColumns ).isInstanceOf( List.class );

	}

	@Test
	void testDefaultValues() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext();
		final DynamicClassDetails dynamicEntity = new DynamicClassDetails( "DynamicEntity", buildingContext );
		final DynamicAnnotationUsage<GeneratedValue> generatorAnn = new DynamicAnnotationUsage<>(
				JpaAnnotations.GENERATED_VALUE,
				buildingContext
		);

		GenerationType strategy = generatorAnn.getAttributeValue( "strategy" );
		assertThat( strategy ).isEqualTo( GenerationType.AUTO );
		String generator = generatorAnn.findAttributeValue( "generator" );
		assertThat( generator ).isEqualTo( "" );
	}

	@Test
	void testClassDetailsDefaultValue(){
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext();
		final DynamicClassDetails dynamicEntity = new DynamicClassDetails( "DynamicEntity", buildingContext );
		final DynamicAnnotationUsage<ElementCollection> elementCollectionAnn = new DynamicAnnotationUsage<>(
				JpaAnnotations.ELEMENT_COLLECTION,
				buildingContext
		);

		Object value = elementCollectionAnn.getAttributeValue( "targetClass" );
		assertThat( value ).isInstanceOf( ClassDetails.class );
	}
}
