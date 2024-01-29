/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * Member used to represent map key access for dynamic models ("MAP mode")
 *
 * @author Steve Ebersole
 */
public class MapModeFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final String name;
	private final TypeDetails type;
	private final int modifierFlags;
	private final ClassDetails declaringType;

	private final boolean isArray;
	private final boolean isPlural;

	public MapModeFieldDetails(
			String name,
			TypeDetails type,
			int modifierFlags,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.modifierFlags = modifierFlags;
		this.declaringType = declaringType;

		if ( type != null ) {
			this.isArray = type.getName().startsWith( "[" );
			this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
		}
		else {
			this.isArray = false;
			this.isPlural = false;
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public TypeDetails getType() {
		return type;
	}

	@Override
	public ClassDetails getDeclaringType() {
		return declaringType;
	}

	@Override
	public boolean isPlural() {
		return isPlural;
	}

	@Override
	public boolean isArray() {
		return isArray;
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
	public Member toJavaMember() {
		return null;
	}

	@Override
	public String toString() {
		return "MapModeFieldDetails(" + name + ")";
	}
}
