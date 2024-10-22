/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.Objects;

import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

/**
 * @author Steve Ebersole
 */
public class ArrayTypeDetailsImpl implements ArrayTypeDetails {
	private final ClassDetails arrayClassDetails;
	private final TypeDetails constituentType;
	private final int dimensions;

	public ArrayTypeDetailsImpl(ClassDetails arrayClassDetails, TypeDetails constituentType) {
		this.arrayClassDetails = arrayClassDetails;
		this.constituentType = constituentType;
		this.dimensions = StringHelper.countArrayDimensions( arrayClassDetails.getName() );
	}

	@Override
	public ClassDetails getArrayClassDetails() {
		return arrayClassDetails;
	}

	@Override
	public TypeDetails getConstituentType() {
		return constituentType;
	}

	@Override
	public int getDimensions() {
		return dimensions;
	}

	@Override
	public String getName() {
		return arrayClassDetails.getName();
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		if ( ClassDetails.OBJECT_CLASS_DETAILS.isImplementor( checkType ) ) {
			return true;
		}

		if ( !checkType.isArray() ) {
			return false;
		}

		return constituentType.isImplementor( checkType.getComponentType() );
	}

	@Override
	public TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable) {
		if ( constituentType.getTypeKind() == Kind.PARAMETERIZED_TYPE ) {
			return constituentType.asParameterizedType().resolveTypeVariable( typeVariable );
		}
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if ( this == o ) {
			return true;
		}
		if ( o == null || getClass() != o.getClass() ) {
			return false;
		}
		ArrayTypeDetailsImpl that = (ArrayTypeDetailsImpl) o;
		return Objects.equals( arrayClassDetails, that.arrayClassDetails );
	}

	@Override
	public int hashCode() {
		return Objects.hash( arrayClassDetails );
	}
}
