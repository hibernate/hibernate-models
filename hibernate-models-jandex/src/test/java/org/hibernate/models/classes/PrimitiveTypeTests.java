/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.classes;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class PrimitiveTypeTests {
	@Test
	void testWithJandex() {
		final Index jandexIndex = SourceModelTestHelper.buildJandexIndex();
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex );
		verify( buildingContext.getClassDetailsRegistry() );
	}

	@Test
	void testWithoutJandex() {
		final SourceModelBuildingContext buildingContext = createBuildingContext( (Index) null );
		verify( buildingContext.getClassDetailsRegistry() );
	}

	void verify(ClassDetailsRegistry classDetailsRegistry) {
		final ClassDetails booleanDetails = classDetailsRegistry.resolveClassDetails( "boolean" );
		assertThat( booleanDetails ).isNotNull();
		assertThat( booleanDetails ).isInstanceOf( JdkClassDetails.class );

		final ClassDetails byteDetails = classDetailsRegistry.resolveClassDetails( "byte" );
		assertThat( byteDetails ).isNotNull();
		assertThat( byteDetails ).isInstanceOf( JdkClassDetails.class );

		final ClassDetails shortDetails = classDetailsRegistry.resolveClassDetails( "short" );
		assertThat( shortDetails ).isNotNull();
		assertThat( shortDetails ).isInstanceOf( JdkClassDetails.class );

		final ClassDetails intDetails = classDetailsRegistry.resolveClassDetails( "int" );
		assertThat( intDetails ).isNotNull();
		assertThat( intDetails ).isInstanceOf( JdkClassDetails.class );

		final ClassDetails longDetails = classDetailsRegistry.resolveClassDetails( "long" );
		assertThat( longDetails ).isNotNull();
		assertThat( longDetails ).isInstanceOf( JdkClassDetails.class );

		final ClassDetails doubleDetails = classDetailsRegistry.resolveClassDetails( "double" );
		assertThat( doubleDetails ).isNotNull();
		assertThat( doubleDetails ).isInstanceOf( JdkClassDetails.class );

		final ClassDetails floatDetails = classDetailsRegistry.resolveClassDetails( "float" );
		assertThat( floatDetails ).isNotNull();
		assertThat( floatDetails ).isInstanceOf( JdkClassDetails.class );
	}
}
