/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import org.hibernate.models.ModelsException;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelContext;

/**
 * @author Steve Ebersole
 */
public class ReflectionHelper {
	public static Method resolveJavaMember(
			MethodDetails methodDetails,
			Class<?> declaringClass,
			ClassLoading classLoading,
			SourceModelContext modelContext) {
		final MethodDetails.MethodKind methodKind = methodDetails.getMethodKind();
		try {
			if ( methodKind == MethodDetails.MethodKind.GETTER ) {
				// make sure the type ends up on the given class-loading
				methodDetails.getType().determineRawClass().toJavaClass( classLoading, modelContext );
				return declaringClass.getDeclaredMethod( methodDetails.getName() );
			}
			else if ( methodKind == MethodDetails.MethodKind.SETTER ) {
				final Class<?> memberTypeClass = methodDetails.getType().determineRawClass().toJavaClass( classLoading, modelContext );
				return declaringClass.getDeclaredMethod( methodDetails.getName(), memberTypeClass );
			}
			else {
				final List<Class<?>> argumentClasses = CollectionHelper.arrayList( methodDetails.getArgumentTypes().size() );
				methodDetails.getArgumentTypes().forEach( (argumentClassDetails) -> {
					argumentClasses.add( argumentClassDetails.toJavaClass( classLoading, modelContext ) );
				} );
				return declaringClass.getDeclaredMethod( methodDetails.getName(), argumentClasses.toArray( new Class[0] ) );
			}
		}
		catch (NoSuchMethodException e) {
			throw new ModelsException(
					String.format(
							Locale.ROOT,
							"Unable to locate method `%s` on %s",
							methodDetails.getName(),
							declaringClass.getName()
					),
					e
			);
		}

	}
}
