/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.jdk;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.models.internal.TypeVariableReferenceDetailsImpl;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

/**
 * @author Steve Ebersole
 */
public class JdkTrackingTypeSwitcher implements JdkTypeSwitcher {
	private final JdkTrackingTypeSwitch typeSwitch;

	private List<String> typeVariableIdentifiers;
	private Map<String, List<TypeVariableReferenceDetailsImpl>> typeVariableRefXref;

	public static TypeDetails standardSwitchType(
			Type type,
			SourceModelBuildingContext buildingContext) {
		return new JdkTrackingTypeSwitcher( buildingContext ).switchType( type );
	}

	public JdkTrackingTypeSwitcher(SourceModelBuildingContext buildingContext) {
		typeSwitch = new JdkTrackingTypeSwitch( this, buildingContext );
	}

	@Override
	public TypeDetails switchType(Type type) {
		//noinspection rawtypes
		if ( type instanceof Class classType ) {
			return typeSwitch.caseClass( classType );
		}

		if ( type instanceof GenericArrayType arrayType ) {
			return typeSwitch.caseGenericArrayType( arrayType );
		}

		if ( type instanceof ParameterizedType parameterizedType ) {
			return typeSwitch.caseParameterizedType( parameterizedType );
		}

		//noinspection rawtypes
		if ( type instanceof TypeVariable typeVariable ) {
			return switchTypeVariable( type, typeVariable );
		}

		if ( type instanceof WildcardType wildcardType ) {
			return typeSwitch.caseWildcardType( wildcardType );
		}

		return typeSwitch.defaultCase( type );
	}

	private TypeDetails switchTypeVariable(Type type, @SuppressWarnings("rawtypes") TypeVariable typeVariable) {
		if ( typeVariableIdentifiers == null ) {
			typeVariableIdentifiers = new ArrayList<>();
		}
		else {
			if ( typeVariableIdentifiers.contains( typeVariable.getTypeName() ) ) {
				// this should indicate a "recursive" type var (e.g. `T extends Comparable<T>`)
				final TypeVariableReferenceDetailsImpl reference = new TypeVariableReferenceDetailsImpl( type.getTypeName() );
				if ( typeVariableRefXref == null ) {
					typeVariableRefXref = new HashMap<>();
					final List<TypeVariableReferenceDetailsImpl> list = typeVariableRefXref.computeIfAbsent(
							type.getTypeName(),
							(s) -> new ArrayList<>()
					);
					list.add( reference );
				}
				return reference;
			}
		}
		typeVariableIdentifiers.add( typeVariable.getTypeName() );

		final TypeVariableDetails switched = typeSwitch.caseTypeVariable( typeVariable );
		assert switched != null;

		if ( typeVariableRefXref != null ) {
			final List<TypeVariableReferenceDetailsImpl> list = typeVariableRefXref.get( typeVariable.getTypeName() );
			if ( CollectionHelper.isNotEmpty( list ) ) {
				for ( TypeVariableReferenceDetailsImpl reference : list ) {
					reference.setTarget( switched );
				}
			}
		}

		return switched;
	}
}
