/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations;

import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.dynamic.DynamicAnnotationUsage;
import org.hibernate.models.internal.dynamic.DynamicClassDetails;
import org.hibernate.models.orm.JpaAnnotations;

import org.junit.jupiter.api.Test;

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
				dynamicEntity
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
}
