/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.annotations.target;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * NOTE : the actual placement of annotations is irrelevant for these test - we are simply walking containers
 *
 * @author Steve Ebersole
 */
public class WalkContainerTests {
	@Test
	void testNoPackageCrossingWithJandex() {
		testNoPackageCrossing( buildJandexIndex( NoGeneratorEntity.class ) );
	}

	@Test
	void testNoPackageCrossingWithoutJandex() {
		testNoPackageCrossing( null );
	}

	private void testNoPackageCrossing(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final List<AnnotationTarget> collected = new ArrayList<>();

		// starting from the class, we should get 2 - the class and  the package
		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		entityClass.walkSelfAndContainers( false, buildingContext, collected::add );
		assertThat( collected ).hasSize( 2 );

		collected.clear();

		// starting from the member, we should get 3 containers - this member, the class and the package
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		idMember.walkSelfAndContainers( false, buildingContext, collected::add );
		assertThat( collected ).hasSize( 3 );
	}
	@Test
	void testPackageCrossingWithJandex() {
		testPackageCrossing( buildJandexIndex( NoGeneratorEntity.class ) );
	}

	@Test
	void testPackageCrossingWithoutJandex() {
		testPackageCrossing( null );
	}

	private void testPackageCrossing(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final List<AnnotationTarget> collected = new ArrayList<>();

		// starting from the class, we should get 6 containers -
		//		* org.hibernate.models.annotations.target.NoGeneratorEntity
		//		* org.hibernate.models.annotations.target
		//		* org.hibernate.models.annotations
		//		* org.hibernate.models
		//		* org.hibernate
		//		* org
		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		entityClass.walkSelfAndContainers( true, buildingContext, collected::add );
		assertThat( collected ).hasSize( 6 );

		collected.clear();

		// starting from the member, we should get 7 containers -
		//		* org.hibernate.models.annotations.target.NoGeneratorEntity#id
		//		* org.hibernate.models.annotations.target.NoGeneratorEntity
		//		* org.hibernate.models.annotations.target
		//		* org.hibernate.models.annotations
		//		* org.hibernate.models
		//		* org.hibernate
		//		* org
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		idMember.walkSelfAndContainers( true, buildingContext, collected::add );
		assertThat( collected ).hasSize( 7 );
	}
}
