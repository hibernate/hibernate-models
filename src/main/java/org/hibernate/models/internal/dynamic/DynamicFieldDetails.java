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

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableScope;

/**
 * FieldDetails which does not necessarily map to a physical Field (dynamic models)
 *
 * @author Steve Ebersole
 */
public class DynamicFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final String name;
	private final TypeDetails type;
	private final ClassDetails declaringType;
	private final int modifierFlags;

	private final boolean isArray;
	private final boolean isPlural;

	public DynamicFieldDetails(
			String name,
			TypeDetails type,
			ClassDetails declaringType,
			int modifierFlags,
			SourceModelBuildingContext buildingContext) {
		this(
				name,
				type,
				declaringType,
				modifierFlags,
				type != null && type.getName().startsWith( "[" ),
				type != null && ( type.isImplementor( Collection.class ) || type.isImplementor( Map.class ) ),
				buildingContext
		);
	}

	public DynamicFieldDetails(
			String name,
			TypeDetails type,
			ClassDetails declaringType,
			int modifierFlags,
			boolean isArray,
			boolean isPlural,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.declaringType = declaringType;
		this.modifierFlags = modifierFlags;
		this.isArray = isArray;
		this.isPlural = isPlural;
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
	public Member toJavaMember() {
		return null;
	}

	@Override
	public TypeDetails resolveRelativeType(TypeVariableScope container) {
		return type;
	}

	@Override
	public String toString() {
		return "DynamicFieldDetails(" + name + ")";
	}
}
