/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.util.List;

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
	private final MethodKind methodKind;
	private final boolean isPersistable;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	public DynamicMethodDetails(
			String name,
			ClassDetails type,
			MethodKind methodKind,
			boolean isPersistable,
			ClassDetails returnType,
			List<ClassDetails> argumentTypes,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.methodKind = methodKind;
		this.isPersistable = isPersistable;
		this.returnType = returnType;
		this.argumentTypes = argumentTypes;
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
	public ClassDetails getReturnType() {
		return returnType;
	}

	@Override
	public List<ClassDetails> getArgumentTypes() {
		return argumentTypes;
	}

	@Override
	public boolean isPersistable() {
		return isPersistable;
	}
}
