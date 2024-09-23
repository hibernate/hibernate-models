/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ClassDetails;

/**
 * Details about Java primitives
 *
 * @author Steve Ebersole
 */
public enum PrimitiveKind {
	BOOLEAN('Z', boolean.class ),
	BYTE( 'B', byte.class ),
	CHAR( 'C', char.class ),
	SHORT( 'S', short.class ),
	INT( 'I', int.class ),
	LONG( 'J', long.class ),
	DOUBLE( 'D', double.class ),
	FLOAT( 'F', float.class );

	private final char javaTypeChar;
	private final Class<?> javaType;

	PrimitiveKind(char javaTypeChar, Class<?> javaType) {
		this.javaTypeChar = javaTypeChar;
		this.javaType = javaType;
	}

	public char getJavaTypeChar() {
		return javaTypeChar;
	}

	public String getTypeName() {
		return javaType.getName();
	}

	public static PrimitiveKind resolveFromTypeChar(char javaTypeChar) {
		for ( PrimitiveKind primitiveKind : PrimitiveKind.values() ) {
			if ( primitiveKind.javaTypeChar == javaTypeChar ) {
				return primitiveKind;
			}
		}
		throw new IllegalArgumentException( "Unknown primitive Java type character - " + javaTypeChar );
	}

	public static PrimitiveKind resolveFromClassDetails(ClassDetails classDetails) {
		for ( PrimitiveKind primitiveKind : PrimitiveKind.values() ) {
			if ( primitiveKind.javaType.equals( classDetails.toJavaClass() ) ) {
				return primitiveKind;
			}
		}
		throw new IllegalArgumentException( "Unknown primitive class - " + classDetails );
	}
}
