/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.util.Objects;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;

/**
 * @author Steve Ebersole
 */
public class ClassTypeDetailsImpl implements ClassTypeDetails {
	private final ClassDetails classDetails;
	private final Kind kind;

	public ClassTypeDetailsImpl(ClassDetails classDetails, Kind kind) {
		assert classDetails != null;
		assert kind == Kind.CLASS || kind == Kind.PRIMITIVE || kind == Kind.VOID;
		this.classDetails = classDetails;
		this.kind = kind;
	}

	public ClassDetails getClassDetails() {
		return classDetails;
	}

	@Override
	public String getName() {
		return classDetails.getName();
	}

	@Override
	public Kind getTypeKind() {
		return kind;
	}

	@Override
	public String toString() {
		return "ClassTypeDetails(" + classDetails.getName() + ")";
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ClassTypeDetailsImpl that = (ClassTypeDetailsImpl) o;
		return Objects.equals( classDetails, that.classDetails ) && kind == that.kind;
	}

	@Override
	public int hashCode() {
		return Objects.hash( classDetails, kind );
	}
}
