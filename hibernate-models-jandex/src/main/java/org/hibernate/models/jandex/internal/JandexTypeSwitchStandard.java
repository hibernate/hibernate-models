/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.ParameterizedTypeDetailsImpl;
import org.hibernate.models.internal.PrimitiveTypeDetailsImpl;
import org.hibernate.models.internal.TypeVariableDetailsImpl;
import org.hibernate.models.internal.TypeVariableReferenceDetailsImpl;
import org.hibernate.models.internal.VoidTypeDetailsImpl;
import org.hibernate.models.internal.WildcardTypeDetailsImpl;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.jandex.spi.JandexTypeSwitch;
import org.hibernate.models.jandex.spi.JandexTypeSwitcher;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;

import org.jboss.jandex.ArrayType;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.ParameterizedType;
import org.jboss.jandex.PrimitiveType;
import org.jboss.jandex.Type;
import org.jboss.jandex.TypeVariable;
import org.jboss.jandex.TypeVariableReference;
import org.jboss.jandex.VoidType;
import org.jboss.jandex.WildcardType;

import static org.hibernate.models.internal.util.CollectionHelper.arrayList;

/**
 * JandexTypeSwitch implementation which builds corresponding {@linkplain TypeDetails} references
 *
 * @author Steve Ebersole
 */
public class JandexTypeSwitchStandard implements JandexTypeSwitch<TypeDetails> {

	public static TypeDetails switchType(Type type, ClassDetails declaringType, ModelsContext modelsContext) {
		final JandexTypeSwitchStandard genericVariableSwitch = new JandexTypeSwitchStandard( declaringType );
		return JandexTypeSwitcher.switchType( type, genericVariableSwitch, modelsContext );
	}

	private final ClassDetails declaringType;

	private JandexTypeSwitchStandard(ClassDetails declaringType) {
		this.declaringType = declaringType;
	}

	@Override
	public TypeDetails caseClass(ClassType classType, ModelsContext modelsContext) {
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( classType.name().toString() );
		return new ClassTypeDetailsImpl( classDetails, TypeDetails.Kind.CLASS );
	}

	@Override
	public TypeDetails casePrimitive(PrimitiveType primitiveType, ModelsContext modelsContext) {
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( primitiveType.name().toString() );
		return new PrimitiveTypeDetailsImpl( classDetails );
	}

	@Override
	public TypeDetails caseVoid(VoidType voidType, ModelsContext modelsContext) {
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				// allows for void or Void
				.resolveClassDetails( voidType.name().toString() );
		return new VoidTypeDetailsImpl( classDetails );
	}

	@Override
	public TypeDetails caseParameterizedType(
			ParameterizedType parameterizedType,
			ModelsContext modelsContext) {
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( parameterizedType.name().toString() );
		return new ParameterizedTypeDetailsImpl(
				classDetails,
				resolveTypes( parameterizedType.arguments(), this, modelsContext ),
				null
		);
	}

	@Override
	public TypeDetails caseWildcardType(WildcardType wildcardType, ModelsContext modelsContext) {
		try {
			final Type bound = (Type) BOUND_METHOD.invoke( wildcardType );
			final boolean isExtends = (boolean) IS_EXTENDS_METHOD.invoke( wildcardType );
			return new WildcardTypeDetailsImpl( JandexTypeSwitcher.switchType( bound, this, modelsContext ), isExtends );
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException( e );
		}
	}

	@Override
	public TypeDetails caseTypeVariable(TypeVariable typeVariable, ModelsContext modelsContext) {
		return new TypeVariableDetailsImpl(
				typeVariable.identifier(),
				declaringType,
				resolveTypes( typeVariable.bounds(), this, modelsContext )
		);
	}

	@Override
	public TypeDetails caseTypeVariableReference(
			TypeVariableReference typeVariableReference,
			ModelsContext modelsContext) {
		return new TypeVariableReferenceDetailsImpl( typeVariableReference.identifier() );
	}

	@Override
	public TypeDetails caseArrayType(ArrayType arrayType, ModelsContext modelsContext) {
		final TypeDetails componentTypeDetails = JandexTypeSwitcher.switchType( arrayType.componentType(), this, modelsContext );
		return TypeDetailsHelper.arrayOf( componentTypeDetails, modelsContext );
	}

	@Override
	public TypeDetails defaultCase(Type type, ModelsContext modelsContext) {
		throw new UnsupportedOperationException( "Unexpected Type kind - " + type );
	}

	public static List<TypeDetails> resolveTypes(
			List<Type> types,
			JandexTypeSwitch<TypeDetails> typeSwitch,
			ModelsContext modelsContext) {
		if ( CollectionHelper.isEmpty( types ) ) {
			return Collections.emptyList();
		}

		final ArrayList<TypeDetails> result = arrayList( types.size() );
		for ( Type actualTypeArgument : types ) {
			final TypeDetails switchedType = JandexTypeSwitcher.switchType( actualTypeArgument, typeSwitch, modelsContext );
			result.add( switchedType );
		}
		return result;
	}

	private static final Method BOUND_METHOD;
	private static final Method IS_EXTENDS_METHOD;

	static {
		try {
			BOUND_METHOD = WildcardType.class.getDeclaredMethod( "bound" );
			IS_EXTENDS_METHOD = WildcardType.class.getDeclaredMethod( "isExtends" );

			BOUND_METHOD.trySetAccessible();
			IS_EXTENDS_METHOD.trySetAccessible();
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException( e );
		}
	}
}
