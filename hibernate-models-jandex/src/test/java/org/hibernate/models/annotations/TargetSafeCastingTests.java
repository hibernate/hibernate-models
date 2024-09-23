/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.annotations;

import java.lang.annotation.Annotation;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.SimpleEntity;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Steve Ebersole
 */
public class TargetSafeCastingTests {
	@Test
	void testSafeCastingWithJandex() {
		safeCastingTests( buildJandexIndex( SimpleEntity.class ) );
	}
	@Test
	void testSafeCastingWithoutJandex() {
		safeCastingTests( null );
	}

	void safeCastingTests(Index jandexIndex) {
		final SourceModelBuildingContext buildingContext = createBuildingContext( jandexIndex, SimpleEntity.class );

		checkCasting( JpaAnnotations.ID );

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( SimpleEntity.class.getName() );

		checkCasting( classDetails );
		checkCasting( classDetails.getFields().get( 0 ) );
		checkCasting( classDetails.getMethods().get( 0 ) );

	}

	private void checkCasting(AnnotationDescriptor<? extends Annotation> annotationDescriptor) {
		assertThat( annotationDescriptor.asAnnotationDescriptor() ).isSameAs( annotationDescriptor );

		try {
			annotationDescriptor.asClassDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			annotationDescriptor.asMemberDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			annotationDescriptor.asFieldDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			annotationDescriptor.asMethodDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			annotationDescriptor.asRecordComponentDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}
	}

	private void checkCasting(ClassDetails classDetails) {
		assertThat( classDetails.asClassDetails() ).isSameAs( classDetails );

		try {
			classDetails.asAnnotationDescriptor();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			classDetails.asMemberDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			classDetails.asFieldDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			classDetails.asMethodDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			classDetails.asRecordComponentDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}
	}

	private void checkCasting(FieldDetails fieldDetails) {
		assertThat( fieldDetails.asFieldDetails() ).isSameAs( fieldDetails );
		assertThat( fieldDetails.asMemberDetails() ).isSameAs( fieldDetails );

		try {
			fieldDetails.asAnnotationDescriptor();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			fieldDetails.asClassDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			fieldDetails.asMethodDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			fieldDetails.asRecordComponentDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}
	}

	private void checkCasting(MethodDetails methodDetails) {
		assertThat( methodDetails.asMethodDetails() ).isSameAs( methodDetails );
		assertThat( methodDetails.asMemberDetails() ).isSameAs( methodDetails );

		try {
			methodDetails.asAnnotationDescriptor();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			methodDetails.asClassDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			methodDetails.asFieldDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}

		try {
			methodDetails.asRecordComponentDetails();
			fail( "Expecting IllegalCastException" );
		}
		catch (IllegalCastException expected) {
		}
	}
}
