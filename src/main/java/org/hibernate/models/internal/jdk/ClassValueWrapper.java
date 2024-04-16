/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueWrapper;

/**
 * @author Steve Ebersole
 */
public class ClassValueWrapper implements ValueWrapper<ClassDetails,Class<?>> {
	public static final ClassValueWrapper JDK_CLASS_VALUE_WRAPPER = new ClassValueWrapper();

	@Override
	public ClassDetails wrap(Class<?> rawValue, SourceModelBuildingContext buildingContext) {
		return buildingContext.getClassDetailsRegistry().resolveClassDetails( rawValue.getName() );
	}
}
