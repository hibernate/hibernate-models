/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;

import org.hibernate.models.IllegalCastException;

/**
 * Extension of ClassDetails which allows manipulation of the members
 *
 * @author Steve Ebersole
 */
public interface MutableClassDetails extends ClassDetails, MutableAnnotationTarget {
	default void clearMemberAnnotationUsages() {
		forEachField( (i, field) -> ( (MutableAnnotationTarget) field ).clearAnnotationUsages() );
		forEachMethod( (i, method) -> ( (MutableAnnotationTarget) method ).clearAnnotationUsages() );
	}

	void addField(FieldDetails fieldDetails);
	void addMethod(MethodDetails methodDetails);

	@Override
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		return ClassDetails.super.asAnnotationDescriptor();
	}

	@Override
	default MutableClassDetails asClassDetails() {
		return this;
	}

	@Override
	default MutableMemberDetails asMemberDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to MemberDescriptor" );
	}

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "ClassDetails cannot be cast to RecordComponentDetails" );
	}
}
