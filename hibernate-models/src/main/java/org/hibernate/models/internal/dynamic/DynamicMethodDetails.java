/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableScope;

/**
 * MethodDetails which does not necessarily map to a physical Method (dynamic models)
 *
 * @author Steve Ebersole
 */
public class DynamicMethodDetails extends AbstractAnnotationTarget implements MethodDetails, MutableMemberDetails {
	private final String name;
	private final TypeDetails type;
	private final ClassDetails declaringType;
	private final MethodKind methodKind;
	private final int modifierFlags;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	private final boolean isArray;
	private final boolean isPlural;

	public DynamicMethodDetails(
			String name,
			TypeDetails type,
			ClassDetails declaringType,
			MethodKind methodKind,
			int modifierFlags,
			ClassDetails returnType,
			List<ClassDetails> argumentTypes,
			SourceModelBuildingContext buildingContext) {
		this(
				name,
				type,
				declaringType,
				methodKind,
				modifierFlags,
				type != null && type.getName().startsWith( "[" ),
				type != null && ( type.isImplementor( Collection.class ) || type.isImplementor( Map.class ) ),
				returnType,
				argumentTypes,
				buildingContext
		);
	}

	public DynamicMethodDetails(
			String name,
			TypeDetails type,
			ClassDetails declaringType,
			MethodKind methodKind,
			int modifierFlags,
			boolean isArray,
			boolean isPlural,
			ClassDetails returnType,
			List<ClassDetails> argumentTypes,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.declaringType = declaringType;
		this.methodKind = methodKind;
		this.modifierFlags = modifierFlags;
		this.isArray = isArray;
		this.isPlural = isPlural;
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

	@Override
	public MethodDetails asMethodDetails() {
		return this;
	}

	@Override
	public MutableMemberDetails asMemberDetails() {
		return this;
	}

	@Override
	public FieldDetails asFieldDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast as FieldDetails" );
	}

	@Override
	public RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast as RecordComponentDetails" );
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast as ClassDetails" );
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "MethodDetails cannot be cast as AnnotationDescriptor" );
	}
}
