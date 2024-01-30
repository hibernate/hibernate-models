/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.util.Objects;

import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.TypeDetails;

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
		StringBuilder builder = new StringBuilder();

		TypeDetails type = this;
		while ( type.getTypeKind() == Kind.ARRAY ) {
			int dimensions = type.asArrayType().getDimensions();
			while ( dimensions-- > 0 ) {
				builder.append( '[' );
			}
			type = type.asArrayType().getConstituentType();
		}

		// here, `type` is an element type of the array, i.e., never array
		if ( type.getTypeKind() == Kind.PRIMITIVE ) {
			builder.append( type.asPrimitiveType().toCode() );
		}
		else {
			// This relies on name() representing the erased type name
			// For historical 1.x reasons, we follow the Java reflection format
			// instead of the Java descriptor format.
			builder.append( 'L' ).append( type.getName() ).append( ';' );
		}

		return builder.toString();
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
	public TypeDetails resolveTypeVariable(String identifier) {
		if ( constituentType.getTypeKind() == Kind.PARAMETERIZED_TYPE ) {
			return constituentType.asParameterizedType().resolveTypeVariable( identifier );
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
