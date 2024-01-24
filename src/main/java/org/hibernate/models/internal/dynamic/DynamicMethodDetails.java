/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class DynamicMethodDetails extends AbstractAnnotationTarget implements MethodDetails, MutableMemberDetails {
	private final String name;
	private final ClassDetails type;
	private final ClassDetails declaringType;
	private final MethodKind methodKind;
	private final int modifierFlags;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	private final boolean isArray;
	private final boolean isPlural;

	public DynamicMethodDetails(
			String name,
			ClassDetails type,
			ClassDetails declaringType,
			MethodKind methodKind,
			int modifierFlags,
			ClassDetails returnType,
			List<ClassDetails> argumentTypes,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.declaringType = declaringType;
		this.methodKind = methodKind;
		this.modifierFlags = modifierFlags;
		this.returnType = returnType;
		this.argumentTypes = argumentTypes;

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
	public MethodKind getMethodKind() {
		return methodKind;
	}

	@Override
	public ClassDetails getType() {
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
	public ClassDetails getReturnType() {
		return returnType;
	}

	@Override
	public List<ClassDetails> getArgumentTypes() {
		return argumentTypes;
	}

	@Override
	public String toString() {
		return "DynamicMethodDetails(" + name + ")";
	}
}
