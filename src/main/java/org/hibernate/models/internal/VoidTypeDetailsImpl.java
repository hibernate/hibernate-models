/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.util.Objects;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.VoidTypeDetails;

/**
 * TypeDetails referring to a void or Void
 *
 * @author Steve Ebersole
 */
public class VoidTypeDetailsImpl implements VoidTypeDetails {
	private final ClassDetails voidClassDetails;

	public VoidTypeDetailsImpl(ClassDetails voidClassDetails) {
		this.voidClassDetails = voidClassDetails;
	}

	@Override
	public ClassDetails getClassDetails() {
		return voidClassDetails;
	}

	@Override
	public Kind getTypeKind() {
		return Kind.VOID;
	}

	@Override
	public VoidTypeDetails asVoidType() {
		return this;
	}

	@Override
	public TypeDetails resolveTypeVariable(String identifier) {
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		VoidTypeDetailsImpl that = (VoidTypeDetailsImpl) o;
		return Objects.equals( voidClassDetails, that.voidClassDetails );
	}

	@Override
	public int hashCode() {
		return Objects.hash( voidClassDetails );
	}
}
