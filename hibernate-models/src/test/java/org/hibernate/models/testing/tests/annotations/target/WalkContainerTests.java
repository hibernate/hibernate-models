/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations.target;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * NOTE : the actual placement of annotations is irrelevant for these test - we are simply walking containers
 *
 * @author Steve Ebersole
 */
public class WalkContainerTests {
	@Test
	void testNoPackageCrossing() {
		final ModelsContext modelsContext = createModelContext( NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final List<AnnotationTarget> collected = new ArrayList<>();

		// starting from the class, we should get 2 - the class and  the package
		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		entityClass.walkSelfAndContainers( false, modelsContext, collected::add );
		assertThat( collected ).hasSize( 2 );

		collected.clear();

		// starting from the member, we should get 3 containers - this member, the class and the package
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		idMember.walkSelfAndContainers( false, modelsContext, collected::add );
		assertThat( collected ).hasSize( 3 );
	}
	@Test
	void testPackageCrossing() {
		final ModelsContext modelsContext = createModelContext( NoGeneratorEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final List<AnnotationTarget> collected = new ArrayList<>();

		// starting from the class, we should get 8 containers -
		//		* org.hibernate.models.testing.tests.annotations.target.NoGeneratorEntity
		//		* org.hibernate.models.testing.tests.annotations.target
		//		* org.hibernate.models.testing.tests.annotations
		//		* org.hibernate.models.testing.tests
		//		* org.hibernate.models.testing
		//		* org.hibernate.models
		//		* org.hibernate
		//		* org
		final ClassDetails entityClass = classDetailsRegistry.getClassDetails( NoGeneratorEntity.class.getName() );
		entityClass.walkSelfAndContainers( true, modelsContext, collected::add );
		assertThat( collected ).hasSize( 8 );

		collected.clear();

		// starting from the member, we should get 9 containers -
		//		* org.hibernate.models.testing.tests.annotations.target.NoGeneratorEntity#id
		//		* org.hibernate.models.testing.tests.annotations.target.NoGeneratorEntity
		//		* org.hibernate.models.testing.tests.annotations.target
		//		* org.hibernate.models.testing.tests.annotations
		//		* org.hibernate.models.testing.tests
		//		* org.hibernate.models.testing
		//		* org.hibernate.models
		//		* org.hibernate
		//		* org
		final FieldDetails idMember = entityClass.findFieldByName( "id" );
		idMember.walkSelfAndContainers( true, modelsContext, collected::add );
		assertThat( collected ).hasSize( 9 );
	}
}
