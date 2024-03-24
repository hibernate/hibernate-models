/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.Objects;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

public class IsBoundTypeSwitch extends IsResolvedTypeSwitch{
	public static final IsBoundTypeSwitch IS_BOUND_SWITCH = new IsBoundTypeSwitch();

	@Override
	public Boolean caseClass(ClassTypeDetails classType, SourceModelBuildingContext buildingContext) {
		// not completely kosher, but works for our needs
		return !Objects.equals( classType.getClassDetails(), ClassDetails.OBJECT_CLASS_DETAILS );
	}
}
