/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jdk;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.jdk.internal.AbstractJdkAnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableConstructorDetails;
import org.hibernate.models.spi.MutableMemberDetails;

/**
 * @author Steve Ebersole
 */
public class JdkConstructorDetails extends AbstractJdkAnnotationTarget implements MutableConstructorDetails {
	private final Constructor<?> constructor;
	private final ClassDetails declaringType;
	private final List<ClassDetails> argumentTypes;

	public JdkConstructorDetails(
			@Nonnull Constructor<?> constructor,
			@Nonnull ClassDetails declaringType,
			@Nonnull ModelsContext modelsContext) {
		super( constructor::getAnnotations, modelsContext );
		this.constructor = constructor;
		this.declaringType = declaringType;

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		this.argumentTypes = new ArrayList<>( constructor.getParameterCount() );
		for ( int i = 0; i < constructor.getParameterTypes().length; i++ ) {
			argumentTypes.add( classDetailsRegistry.resolveClassDetails( constructor.getParameterTypes()[i].getName() ) );
		}
	}

	@Nonnull
	@Override
	public String getName() {
		return constructor.getName();
	}

	@Nonnull
	@Override
	public ClassDetails getDeclaringType() {
		return declaringType;
	}

	@Nonnull
	@Override
	public List<ClassDetails> getArgumentTypes() {
		return argumentTypes;
	}

	@Nonnull
	@Override
	public Constructor<?> toJavaConstructor() {
		return constructor;
	}

	@Nonnull
	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "ConstructorDetails cannot be cast as ClassDetails" );
	}

	@Nonnull
	@Override
	public MutableMemberDetails asMemberDetails() {
		throw new IllegalCastException( "ConstructorDetails cannot be cast as MemberDetails" );
	}
}
