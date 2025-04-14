/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import java.lang.annotation.Annotation;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.domain.SimpleEntity;
import org.hibernate.models.testing.orm.JpaAnnotations;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Steve Ebersole
 */
public class TargetSafeCastingTests {
	@Test
	public void testSafeCasting() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );

		checkCasting( JpaAnnotations.ID );

		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().getClassDetails( SimpleEntity.class.getName() );

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
