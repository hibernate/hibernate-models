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
}
