/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

/**
 * @author Steve Ebersole
 */
public class TypeHelper {
	public static boolean isResolved(Type type) {
		if ( type instanceof Class ) {
			return true;
		}

		if ( type instanceof GenericArrayType genericArrayType ) {
			return isResolved( genericArrayType.getGenericComponentType() );
		}

		if ( type instanceof ParameterizedType parameterizedType ) {
			Type[] typeArgs = parameterizedType.getActualTypeArguments();
			for ( Type arg : typeArgs ) {
				if ( !isResolved( arg ) ) {
					return false;
				}
			}
			return isResolved( parameterizedType.getRawType() );
		}

		if ( type instanceof TypeVariable<?> ) {
			return false;
		}

		if ( type instanceof WildcardType wildcardType ) {
			return areResolved( wildcardType.getUpperBounds() ) && areResolved( wildcardType.getLowerBounds() );
		}

		return false;
	}

	private static Boolean areResolved(Type[] types) {
		for ( Type t : types ) {
			if ( !isResolved( t ) ) {
				return false;
			}
		}
		return true;
	}

}
