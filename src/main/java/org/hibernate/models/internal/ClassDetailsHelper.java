/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ClassDetails;

/**
 * @author Steve Ebersole
 */
public class ClassDetailsHelper {
	public static boolean isImplementor(Class<?> checkType, ClassDetails classDetails) {
		if ( classDetails.getClassName().equals( checkType.getName() ) ) {
			return true;
		}

		if ( classDetails.getSuperType() != null  ) {
			if ( classDetails.getSuperType().isImplementor( checkType ) ) {
				return true;
			}
		}

		for ( int i = 0; i < classDetails.getImplementedInterfaceTypes().size(); i++ ) {
			final ClassDetails interfaceDetails = classDetails.getImplementedInterfaceTypes().get( i );
			if ( interfaceDetails.isImplementor( checkType ) ) {
				return true;
			}
		}

		return false;
	}

	public static boolean isImplementor(ClassDetails checkType, ClassDetails classDetails) {
		if ( classDetails.getClassName().equals( checkType.getClassName() ) ) {
			return true;
		}

		if ( classDetails.getSuperType() != null  ) {
			if ( classDetails.getSuperType().isImplementor( checkType ) ) {
				return true;
			}
		}

		for ( int i = 0; i < classDetails.getImplementedInterfaceTypes().size(); i++ ) {
			final ClassDetails interfaceDetails = classDetails.getImplementedInterfaceTypes().get( i );
			if ( interfaceDetails.isImplementor( checkType ) ) {
				return true;
			}
		}

		return false;
	}
}
