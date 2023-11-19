/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.internal.jdk.VoidClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

/**
 * @author Steve Ebersole
 */
public class JandexMethodDetails extends AbstractAnnotationTarget implements MethodDetails, MutableMemberDetails {
	private final MethodInfo methodInfo;
	private final MethodKind methodKind;
	private final ClassDetails type;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	public JandexMethodDetails(
			MethodInfo methodInfo,
			MethodKind methodKind,
			ClassDetails type,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.methodInfo = methodInfo;
		this.methodKind = methodKind;
		this.type = type;

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		this.returnType = classDetailsRegistry.resolveClassDetails( methodInfo.returnType().name().toString() );

		this.argumentTypes = new ArrayList<>( methodInfo.parametersCount() );
		for ( int i = 0; i < methodInfo.parametersCount(); i++ ) {
			argumentTypes.add( classDetailsRegistry.resolveClassDetails( methodInfo.parameterType( i ).name().toString() ) );
		}
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return methodInfo;
	}

	@Override
	public String getName() {
		return methodInfo.name();
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
		return methodInfo.flags();
	}

	@Override
	public ClassDetails getReturnType() {
		return returnType;
	}

	@Override
	public List<ClassDetails> getArgumentTypes() {
		return argumentTypes;
	}
}
