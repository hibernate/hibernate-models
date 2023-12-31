/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class JdkMethodDetails extends AbstractAnnotationTarget implements MethodDetails, MutableMemberDetails {
	private final Method method;
	private final MethodKind methodKind;
	private final ClassDetails type;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	public JdkMethodDetails(
			Method method,
			MethodKind methodKind,
			ClassDetails type,
			SourceModelBuildingContext buildingContext) {
		super( method::getAnnotations, buildingContext );
		this.method = method;
		this.methodKind = methodKind;
		this.type = type;

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		this.returnType = classDetailsRegistry.resolveClassDetails( method.getReturnType().getName() );

		this.argumentTypes = new ArrayList<>( method.getParameterCount() );
		for ( int i = 0; i < method.getParameterTypes().length; i++ ) {
			argumentTypes.add( classDetailsRegistry.resolveClassDetails( method.getParameterTypes()[i].getName() ) );
		}
	}

	public Method getMethod() {
		return method;
	}

	@Override
	public String getName() {
		return method.getName();
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
	public int getModifiers() {
		return method.getModifiers();
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
		return String.format(
				Locale.ROOT,
				"JdkMethodDetails( [%s] %s )",
				methodKind.name(),
				method.toString()
		);
	}
}
