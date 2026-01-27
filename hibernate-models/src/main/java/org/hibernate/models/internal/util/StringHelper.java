/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.util;

/**
 * @author Steve Ebersole
 */
public class StringHelper {
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty();
	}

	public static boolean isNotEmpty(String string) {
		return string != null && !string.isEmpty();
	}

	public static String qualifier(String qualifiedName) {
		int loc = qualifiedName.lastIndexOf( '.' );
		return ( loc < 0 ) ? "" : qualifiedName.substring( 0, loc );
	}

	public static String classNameToResourceName(String className) {
		return className.replace( '.', '/' ) + ".class";
	}

	public static int countArrayDimensions(String name) {
		final int lastIndex = name.lastIndexOf( '[' );
		return lastIndex + 1;
	}

	public static char lastCharacter(String text) {
		return text.charAt( text.length() - 1 );
	}

	// See https://github.com/hibernate/hibernate-validator/blob/3f8dc0abec5d81e62761003cacbb288a24ab7c59/engine/src/main/java/org/hibernate/validator/internal/util/StringHelper.java#L87-L94
	public static String decapitalize(String string) {
		if ( string == null || string.isEmpty() || startsWithSeveralUpperCaseLetters( string ) ) {
			return string;
		}
		else {
			return Character.toLowerCase( string.charAt( 0 ) ) + string.substring( 1 );
		}
	}

	private static boolean startsWithSeveralUpperCaseLetters(String string) {
		return string.length() > 1
				&& Character.isUpperCase( string.charAt( 0 ) )
				&& Character.isUpperCase( string.charAt( 1 ) );
	}
}
