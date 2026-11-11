/*
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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.IllegalCastException;
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

import static org.hibernate.models.internal.util.ReflectionHelper.resolveJavaMember;
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

	/**
	 * Constructs method details.
	 *
	 * @param type The attribute type for a getter or setter; may be {@code null} for other methods
	 */
	public JdkMethodDetails(
			@Nonnull Method method,
			@Nonnull MethodKind methodKind,
			@Nullable TypeDetails type,
			@Nonnull ClassDetails declaringType,
			@Nonnull ModelsContext modelsContext) {
		super( method::getAnnotations, modelsContext );
		this.method = method;
		this.methodKind = methodKind;
		this.type = type;
		this.declaringType = declaringType;

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

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

	@Nonnull
	public Method getMethod() {
		return method;
	}

	@Nonnull
	@Override
	public String getName() {
		return method.getName();
	}

	@Nonnull
	@Override
	public MethodKind getMethodKind() {
		return methodKind;
	}

	@Nullable
	@Override
	public TypeDetails getType() {
		return type;
	}

	@Nonnull
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

	@Nonnull
	@Override
	public Method toJavaMember() {
		return method;
	}

	@Nonnull
	@Override
	public Method toJavaMember(@Nonnull Class<?> declaringClass, @Nonnull ClassLoading classLoading, @Nonnull ModelsContext modelContext) {
		if ( declaringClass == method.getDeclaringClass() ) {
			return method;
		}
		return resolveJavaMember( this, declaringClass, classLoading, modelContext );
	}

	@Nullable
	@Override
	public TypeDetails resolveRelativeType(@Nonnull TypeVariableScope container) {
		if ( methodKind == GETTER || methodKind == SETTER ) {
			return type.determineRelativeType( container );
		}

		throw new IllegalStateException( "Method does not have a type - " + this );
	}

	@Nonnull
	@Override
	public ClassBasedTypeDetails resolveRelativeClassType(@Nonnull TypeVariableScope container) {
		if ( methodKind == GETTER || methodKind == SETTER ) {
			return TypeDetailsHelper.resolveRelativeClassType( type, container );
		}
		throw new IllegalStateException( "Method does not have a type - " + this );
	}

	@Override
	public int getModifiers() {
		return method.getModifiers();
	}

	@Nonnull
	@Override
	public ClassDetails getReturnType() {
		return returnType;
	}

	@Nonnull
	@Override
	public List<ClassDetails> getArgumentTypes() {
		return argumentTypes;
	}

	@Nonnull
	@Override
	public String toString() {
		return String.format(
				Locale.ROOT,
				"JdkMethodDetails( [%s] %s )",
				methodKind.name(),
				method.toString()
		);
	}

	@Nonnull
	@Override
	public MethodDetails asMethodDetails() {
		return this;
	}

	@Nonnull
	@Override
	public MutableMemberDetails asMemberDetails() {
		return this;
	}

	@Nonnull
	@Override
	public FieldDetails asFieldDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast as FieldDetails" );
	}

	@Nonnull
	@Override
	public RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast as RecordComponentDetails" );
	}

	@Nonnull
	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast as ClassDetails" );
	}

	@Nonnull
	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "MethodDetails cannot be cast as AnnotationDescriptor" );
	}
}
