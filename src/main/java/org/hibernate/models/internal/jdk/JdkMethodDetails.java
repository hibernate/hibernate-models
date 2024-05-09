/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;
import org.hibernate.models.spi.TypeVariableScope;

import static org.hibernate.models.spi.MethodDetails.MethodKind.GETTER;
import static org.hibernate.models.spi.MethodDetails.MethodKind.SETTER;

/**
 * @author Steve Ebersole
 */
public class JdkMethodDetails extends AbstractJdkAnnotationTarget implements MethodDetails, MutableMemberDetails {
	private final Method method;
	private final MethodKind methodKind;
	private final TypeDetails type;
	private final ClassDetails declaringType;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	private final boolean isArray;
	private final boolean isPlural;

	public JdkMethodDetails(
			Method method,
			MethodKind methodKind,
			TypeDetails type,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		super( method::getAnnotations, buildingContext );
		this.method = method;
		this.methodKind = methodKind;
		this.type = type;
		this.declaringType = declaringType;

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		this.returnType = classDetailsRegistry.resolveClassDetails( method.getReturnType().getName() );

		this.argumentTypes = new ArrayList<>( method.getParameterCount() );
		for ( int i = 0; i < method.getParameterTypes().length; i++ ) {
			argumentTypes.add( classDetailsRegistry.resolveClassDetails( method.getParameterTypes()[i].getName() ) );
		}


		switch ( methodKind ) {
			case GETTER -> {
				this.isArray = method.getReturnType().isArray();
				this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
			}
			case SETTER -> {
				assert method.getParameterCount() == 1;
				this.isArray = method.getParameterTypes()[0].isArray();
				this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
			}
			default -> {
				this.isArray = false;
				this.isPlural = false;
			}
		}

//		final TypeVariable<Method>[] typeParameters = method.getTypeParameters();
//		for ( TypeVariable<Method> typeParameter : typeParameters ) {
//			typeParameter.
//		}
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
	public Method toJavaMember() {
		return method;
	}

	@Override
	public TypeDetails resolveRelativeType(TypeVariableScope container) {
		if ( methodKind == GETTER || methodKind == SETTER ) {
			return type.determineRelativeType( container );
		}

		throw new IllegalStateException( "Method does not have a type - " + this );
	}

	@Override
	public ClassBasedTypeDetails resolveRelativeClassType(TypeVariableScope container) {
		if ( methodKind == GETTER || methodKind == SETTER ) {
			return TypeDetailsHelper.resolveRelativeClassType( type, container );
		}
		throw new IllegalStateException( "Method does not have a type - " + this );
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
