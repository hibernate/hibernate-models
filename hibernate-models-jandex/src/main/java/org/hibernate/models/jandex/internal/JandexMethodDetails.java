/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.util.ReflectionHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;
import org.hibernate.models.spi.TypeVariableScope;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

import static org.hibernate.models.spi.MethodDetails.MethodKind.GETTER;
import static org.hibernate.models.spi.MethodDetails.MethodKind.SETTER;

/**
 * @author Steve Ebersole
 */
public class JandexMethodDetails extends AbstractAnnotationTarget implements MethodDetails, MutableMemberDetails {
	private final MethodInfo methodInfo;
	private final MethodKind methodKind;
	private final TypeDetails type;
	private final ClassDetails declaringType;

	private final ClassDetails returnType;
	private final List<ClassDetails> argumentTypes;

	private final boolean isArray;
	private final boolean isPlural;

	public JandexMethodDetails(
			MethodInfo methodInfo,
			MethodKind methodKind,
			TypeDetails type,
			ClassDetails declaringType,
			ModelsContext modelsContext) {
		super( modelsContext );
		this.methodInfo = methodInfo;
		this.methodKind = methodKind;
		this.type = type;
		this.declaringType = declaringType;

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		this.returnType = classDetailsRegistry.resolveClassDetails( methodInfo.returnType().name().toString() );

		this.argumentTypes = new ArrayList<>( methodInfo.parametersCount() );
		for ( int i = 0; i < methodInfo.parametersCount(); i++ ) {
			argumentTypes.add( classDetailsRegistry.resolveClassDetails( methodInfo.parameterType( i ).name().toString() ) );
		}

		switch ( methodKind ) {
			case GETTER -> {
				this.isArray = methodInfo.returnType().kind() == Type.Kind.ARRAY;
				this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
			}
			case SETTER -> {
				assert methodInfo.parametersCount() == 1;
				this.isArray = methodInfo.parameterType( 0 ).kind() == Type.Kind.ARRAY;
				this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
			}
			default -> {
				this.isArray = false;
				this.isPlural = false;
			}
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
		return methodInfo.flags();
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

	private Method underlyingMethod;

	@Override
	public Method toJavaMember() {
		if ( underlyingMethod == null ) {
			final Class<?> declaringClass = declaringType.toJavaClass();
			underlyingMethod = ReflectionHelper.resolveJavaMember(
					this,
					declaringClass,
					getModelContext().getClassLoading(),
					getModelContext()
			);
		}
		return underlyingMethod;
	}

	@Override
	public Method toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext) {
		return ReflectionHelper.resolveJavaMember(
				this,
				declaringClass,
				getModelContext().getClassLoading(),
				getModelContext()
		);
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
				"JandexMethodDetails( [%s] %s )",
				methodKind.name(),
				methodInfo.toString()
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
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "MethodDetails cannot be cast to an AnnotationDescriptor" );
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast to a ClassDetails" );
	}
}
