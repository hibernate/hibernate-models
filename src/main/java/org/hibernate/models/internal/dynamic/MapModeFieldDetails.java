/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Member used to represent map key access for dynamic models ("MAP mode")
 *
 * @author Steve Ebersole
 */
public class MapModeFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final String name;
	private final ClassDetails type;
	private final int modifierFlags;

	public MapModeFieldDetails(String name, ClassDetails type, int modifierFlags, SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.modifierFlags = modifierFlags;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public int getModifiers() {
		return modifierFlags;
	}

	@Override
	public boolean isPersistable() {
		return true;
	}

	@Override
	public String toString() {
		return "MapModeFieldDetails(" + name + ")";
	}
}
