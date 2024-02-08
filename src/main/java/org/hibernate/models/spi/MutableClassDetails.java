/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

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
}
