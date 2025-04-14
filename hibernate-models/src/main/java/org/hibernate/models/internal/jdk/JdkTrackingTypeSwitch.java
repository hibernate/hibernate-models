/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.GenericDeclaration;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.internal.ArrayTypeDetailsImpl;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.ParameterizedTypeDetailsImpl;
import org.hibernate.models.internal.PrimitiveTypeDetailsImpl;
import org.hibernate.models.internal.TypeVariableDetailsImpl;
import org.hibernate.models.internal.VoidTypeDetailsImpl;
import org.hibernate.models.internal.WildcardTypeDetailsImpl;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

import static org.hibernate.models.internal.jdk.JdkBuilders.isVoid;
import static org.hibernate.models.internal.util.CollectionHelper.arrayList;

/**
 * @author Steve Ebersole
 */
public class JdkTrackingTypeSwitch implements JdkTypeSwitch<TypeDetails> {
	private final JdkTypeSwitcher switcher;
	private final ModelsContext modelsContext;

	public JdkTrackingTypeSwitch(JdkTypeSwitcher switcher, ModelsContext modelsContext) {
		this.switcher = switcher;
		this.modelsContext = modelsContext;
	}

	public ClassBasedTypeDetails caseClass(Class<?> classType) {
		if ( classType.isArray() ) {
			return asArrayType( classType );
		}

		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( classType.getName() );
		if ( classType.isPrimitive() ) {
			return new PrimitiveTypeDetailsImpl( classDetails );
		}
		if ( isVoid( classDetails.toJavaClass() ) ) {
			return new VoidTypeDetailsImpl( classDetails );
		}
		return new ClassTypeDetailsImpl( classDetails, TypeDetails.Kind.CLASS );
	}

	public ParameterizedTypeDetails caseParameterizedType(ParameterizedType parameterizedType) {
		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( parameterizedType.getRawType().getTypeName() );
		return new ParameterizedTypeDetailsImpl(
				classDetails,
				extractTypeParameters( parameterizedType ),
				null
		);
	}

	public WildcardTypeDetails caseWildcardType(WildcardType wildcardType) {
		final Type[] lowerBounds = wildcardType.getLowerBounds();
		if ( CollectionHelper.isEmpty( lowerBounds ) ) {
			// should indicate an extends
			final int numberOfBounds = CollectionHelper.length( wildcardType.getUpperBounds() );
			final TypeDetails upper = numberOfBounds == 1
					? switcher.switchType( wildcardType.getUpperBounds()[0] )
					: ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
			return new WildcardTypeDetailsImpl( upper, true );
		}

		final int numberOfBounds = CollectionHelper.length( lowerBounds );
		final TypeDetails lower = numberOfBounds == 1
				? switcher.switchType( lowerBounds[0] )
				: ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
		return new WildcardTypeDetailsImpl( lower, false );
	}

	public TypeVariableDetails caseTypeVariable(TypeVariable<?> typeVariable) {
		final GenericDeclaration genericDeclaration = typeVariable.getGenericDeclaration();
		final ClassDetails declaringClass;
		if ( genericDeclaration instanceof Class<?> genericClass ) {
			declaringClass = modelsContext.getClassDetailsRegistry().getClassDetails( genericClass.getName() );
		}
		else {
			declaringClass = modelsContext.getClassDetailsRegistry()
					.getClassDetails( ( (Method) genericDeclaration ).getDeclaringClass().getName() );
		}

		return new TypeVariableDetailsImpl(
				typeVariable.getTypeName(),
				declaringClass,
				resolveTypes( typeVariable.getBounds() )
		);
	}

	public TypeDetails caseGenericArrayType(GenericArrayType genericArrayType) {
		final TypeDetails componentType = switcher.switchType( genericArrayType.getGenericComponentType() );
		return TypeDetailsHelper.arrayOf( componentType, modelsContext );
	}

	public TypeDetails defaultCase(Type type) {
		throw new UnsupportedOperationException( "Unexpected Type kind - " + type );
	}

	private ArrayTypeDetails asArrayType(Class<?> arrayClass) {
		assert arrayClass.isArray();
		final ClassDetails arrayClassDetails = modelsContext
				.getClassDetailsRegistry()
				.resolveClassDetails( arrayClass.getName() );
		return new ArrayTypeDetailsImpl(
				arrayClassDetails,
				switcher.switchType( arrayClass.getComponentType() )
		);
	}

	public List<TypeDetails> extractTypeParameters(ParameterizedType parameterizedType) {
		return resolveTypes( parameterizedType.getActualTypeArguments() );
	}

	public List<TypeDetails> resolveTypes(Type[] types) {
		if ( CollectionHelper.isEmpty( types ) ) {
			return Collections.emptyList();
		}

		final ArrayList<TypeDetails> result = arrayList( types.length );
		for ( Type actualTypeArgument : types ) {
			final TypeDetails switchedType = switcher.switchType( actualTypeArgument );
			result.add( switchedType );
		}
		return result;
	}
}
