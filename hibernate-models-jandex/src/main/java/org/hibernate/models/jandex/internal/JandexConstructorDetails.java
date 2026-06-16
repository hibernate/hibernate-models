/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableConstructorDetails;
import org.hibernate.models.spi.MutableMemberDetails;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.MethodInfo;

/**
 * @author Steve Ebersole
 */
public class JandexConstructorDetails extends AbstractAnnotationTarget implements MutableConstructorDetails {
	private final MethodInfo methodInfo;
	private final ClassDetails declaringType;
	private final List<ClassDetails> argumentTypes;

	public JandexConstructorDetails(
			MethodInfo methodInfo,
			ClassDetails declaringType,
			ModelsContext modelsContext) {
		super( modelsContext );
		this.methodInfo = methodInfo;
		this.declaringType = declaringType;

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
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
	public ClassDetails getDeclaringType() {
		return declaringType;
	}

	@Override
	public List<ClassDetails> getArgumentTypes() {
		return argumentTypes;
	}

	@Override
	public Constructor<?> toJavaConstructor() {
		final Class<?>[] arguments = new Class<?>[argumentTypes.size()];
		for ( int i = 0; i < argumentTypes.size(); i++ ) {
			arguments[i] = argumentTypes.get( i ).toJavaClass();
		}

		try {
			return declaringType.toJavaClass().getDeclaredConstructor( arguments );
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( "Unable to resolve constructor - " + this, e );
		}
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "ConstructorDetails cannot be cast as ClassDetails" );
	}

	@Override
	public MutableMemberDetails asMemberDetails() {
		throw new IllegalCastException( "ConstructorDetails cannot be cast as MemberDetails" );
	}
}
