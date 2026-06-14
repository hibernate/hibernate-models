/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ConstructorDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;

import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;

/**
 * @author Steve Ebersole
 */
public class ConstructorDetailsImpl
		extends AbstractAnnotationTarget
		implements ConstructorDetails {
	private final MethodDescription methodDescription;
	private final ClassDetails declaringType;
	private final List<ClassDetails> argumentTypes;

	public ConstructorDetailsImpl(
			MethodDescription methodDescription,
			ClassDetails declaringType,
			ByteBuddyModelsContext modelContext) {
		super( modelContext );
		this.methodDescription = methodDescription;
		this.declaringType = declaringType;

		final ClassDetailsRegistry classDetailsRegistry = modelContext.getClassDetailsRegistry();
		this.argumentTypes = new ArrayList<>( methodDescription.getParameters().size() );
		for ( int i = 0; i < methodDescription.getParameters().size(); i++ ) {
			argumentTypes.add( classDetailsRegistry.resolveClassDetails( methodDescription.getParameters().get( i ).getType().getTypeName() ) );
		}
	}

	@Override
	protected AnnotationSource getAnnotationSource() {
		return methodDescription;
	}

	@Override
	public String getName() {
		return methodDescription.getName();
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
