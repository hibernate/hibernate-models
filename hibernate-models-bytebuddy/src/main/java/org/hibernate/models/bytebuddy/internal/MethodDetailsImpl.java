/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.TypeDetails;

import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;

/**
 * @author Steve Ebersole
 */
public class MethodDetailsImpl extends AbstractAnnotationTarget implements MethodDetails, MutableMemberDetails {
	private final MethodDescription methodDescription;
	private final MethodKind methodKind;
	private final TypeDetails type;
	private final ClassDetails declaringType;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	private final boolean isArray;
	private final boolean isPlural;

	public MethodDetailsImpl(
			MethodDescription methodDescription,
			MethodKind methodKind,
			TypeDetails type,
			ClassDetails declaringType,
			SourceModelBuildingContextImpl modelContext) {
		super( modelContext );
		this.methodDescription = methodDescription;
		this.methodKind = methodKind;
		this.type = type;
		this.declaringType = declaringType;

		final ClassDetailsRegistry classDetailsRegistry = modelContext.getClassDetailsRegistry();
		this.returnType = classDetailsRegistry.resolveClassDetails( methodDescription.getReturnType().getTypeName() );

		this.argumentTypes = new ArrayList<>( methodDescription.getParameters().size() );
		for ( int i = 0; i < methodDescription.getParameters().size(); i++ ) {
			argumentTypes.add( classDetailsRegistry.resolveClassDetails( methodDescription.getParameters().get( i ).getType().getTypeName() ) );
		}

		switch ( methodKind ) {
			case GETTER -> {
				this.isArray = methodDescription.getReturnType().isArray();
				this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
			}
			case SETTER -> {
				assert methodDescription.getParameters().size() == 1;
				final TypeDescription.Generic argumentType = methodDescription.getParameters().asTypeList().get( 0 );

				this.isArray = argumentType.isArray();
				this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
			}
			default -> {
				this.isArray = false;
				this.isPlural = false;
			}
		}
	}

	@Override
	public String getName() {
		return methodDescription.getName();
	}

	@Override
	public MethodKind getMethodKind() {
		return methodKind;
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
	protected AnnotationSource getAnnotationSource() {
		return methodDescription;
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
		return methodDescription.getModifiers();
	}

	private Method underlyingMethod;

	@Override
	public Method toJavaMember() {
		if ( underlyingMethod == null ) {
			underlyingMethod = resolveJavaMember();
		}
		return underlyingMethod;
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
		throw new IllegalCastException( "MethodDetails cannot be cast as FieldDetails" );
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast as FieldDetails" );
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "MethodDetails cannot be cast as AnnotationDescriptor" );
	}

	private Method resolveJavaMember() {
		final Class<?> declaringTypeClass = declaringType.toJavaClass();
		methods: for ( Method method : declaringTypeClass.getDeclaredMethods() ) {
			if ( !method.getName().equals( getName() ) ) {
				continue;
			}

			if ( method.getParameterCount() != methodDescription.getParameters().size() ) {
				continue;
			}

			for ( int i = 0; i < method.getParameterTypes().length; i++ ) {
				final Class<?> methodParameterType = method.getParameterTypes()[i];
				final ParameterDescription parameterDescription = methodDescription.getParameters().get( i );
				if ( !methodParameterType.getName().equals( parameterDescription.getType().getTypeName() ) ) {
					continue methods;
				}
			}

			// if we get here, we've found it
			return method;
		}

		throw new RuntimeException(
				String.format(
						"Jandex FieldInfo had no corresponding Field : %s.%s",
						declaringType.getName(),
						getName()
				)
		);
	}

	@Override
	public String toString() {
		return String.format(
				"MethodDetails( name=%s, kind=%s, type=%s  )",
				methodDescription.getName(),
				methodKind,
				type
		);
	}
}
