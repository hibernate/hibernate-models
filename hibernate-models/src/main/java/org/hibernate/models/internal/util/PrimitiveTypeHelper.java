/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.util;

/**
 * Utilities for dealing with primitive and primitive-wrapper types.
 *
 * @author Steve Ebersole
 */
public class PrimitiveTypeHelper {
	/**
	 * Returns the corresponding {@linkplain Class} reference
	 * for primitive and primitive-wrapper class names.
	 *
	 * @param className The name of the class.
	 *
	 * @return If a primitive or a primitive-wrapper, the corresponding
	 * {@link Class}; {@code null} otherwise.
	 */
	public static Class<?> resolvePrimitiveClass(String className) {
		if ( "boolean".equals( className ) ) {
			return boolean.class;
		}

		if ( Boolean.class.getSimpleName().equalsIgnoreCase( className )
				|| Boolean.class.getName().equals( className ) ) {
			return Boolean.class;
		}

		if ( "byte".equals( className ) ) {
			return byte.class;
		}

		if ( Byte.class.getSimpleName().equals( className )
				|| Byte.class.getName().equals( className ) ) {
			return Byte.class;
		}

		if ( "short".equals( className ) ) {
			return short.class;
		}

		if ( Short.class.getSimpleName().equals( className )
				|| Short.class.getName().equals( className ) ) {
			return Short.class;
		}

		if ( "int".equals( className ) ) {
			return int.class;
		}

		if ( Integer.class.getSimpleName().equals( className )
				|| Integer.class.getName().equals( className ) ) {
			return Integer.class;
		}

		if ( "long".equals( className ) ) {
			return long.class;
		}

		if ( Long.class.getSimpleName().equals( className )
				|| Long.class.getName().equals( className ) ) {
			return Long.class;
		}

		if ( "double".equals( className ) ) {
			return double.class;
		}

		if ( Double.class.getSimpleName().equals( className )
				|| Double.class.getName().equals( className ) ) {
			return Double.class;
		}

		if ( "float".equals( className ) ) {
			return float.class;
		}

		if ( Float.class.getSimpleName().equals( className )
				|| Float.class.getName().equals( className ) ) {
			return Float.class;
		}

		if ( "char".equals( className ) ) {
			return char.class;
		}

		if ( Character.class.getSimpleName().equals( className )
				|| Character.class.getName().equals( className ) ) {
			return Character.class;
		}

		return null;
	}
}
