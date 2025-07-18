/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.internal.TypeVariableReferenceDetailsImpl;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Steve Ebersole
 */
public class JdkTrackingTypeSwitcher implements JdkTypeSwitcher {
	private final JdkTrackingTypeSwitch typeSwitch;

	private Map<String, TypeVariableResolution> typeVariables;
	private Map<String, List<TypeVariableReferenceDetailsImpl>> typeVariableRefXref;

	public static TypeDetails standardSwitchType(
			Type type,
			ModelsContext modelsContext) {
		return new JdkTrackingTypeSwitcher( modelsContext ).switchType( type );
	}

	public JdkTrackingTypeSwitcher(ModelsContext modelsContext) {
		typeSwitch = new JdkTrackingTypeSwitch( this, modelsContext );
	}

	@Override
	public TypeDetails switchType(Type type) {
		if ( type instanceof Class<?> classType ) {
			return typeSwitch.caseClass( classType );
		}

		if ( type instanceof GenericArrayType arrayType ) {
			return typeSwitch.caseGenericArrayType( arrayType );
		}

		if ( type instanceof ParameterizedType parameterizedType ) {
			return typeSwitch.caseParameterizedType( parameterizedType );
		}

		if ( type instanceof TypeVariable<?> typeVariable ) {
			return switchTypeVariable( type, typeVariable );
		}

		if ( type instanceof WildcardType wildcardType ) {
			return typeSwitch.caseWildcardType( wildcardType );
		}

		return typeSwitch.defaultCase( type );
	}

	private TypeDetails switchTypeVariable(Type type, TypeVariable<?> typeVariable) {
		final TypeVariableResolution resolution = new TypeVariableResolution();
		if ( typeVariables == null ) {
			typeVariables = new HashMap<>();
			typeVariables.put( typeVariable.getName(), resolution );
		}
		else {
			final TypeVariableResolution existingResolution = typeVariables.putIfAbsent(
					typeVariable.getTypeName(),
					resolution
			);
			if ( existingResolution != null ) {
				final TypeVariableDetails details = existingResolution.getDetails();
				if ( details != null ) {
					// The type variable has already been switched, so we can return the original details
					return details;
				}
				// this should indicate a "recursive" type var (e.g. `T extends Comparable<T>`)
				final TypeVariableReferenceDetailsImpl reference = new TypeVariableReferenceDetailsImpl( type.getTypeName() );
				if ( typeVariableRefXref == null ) {
					typeVariableRefXref = new HashMap<>();
				}
				final List<TypeVariableReferenceDetailsImpl> list = typeVariableRefXref.computeIfAbsent(
						type.getTypeName(),
						(s) -> new ArrayList<>()
				);
				list.add( reference );
				return reference;
			}
		}

		final TypeVariableDetails switched = typeSwitch.caseTypeVariable( typeVariable );
		assert switched != null;
		resolution.setDetails( switched );

		if ( typeVariableRefXref != null ) {
			final List<TypeVariableReferenceDetailsImpl> list = typeVariableRefXref.remove( typeVariable.getTypeName() );
			if ( list != null ) {
				list.forEach( reference -> reference.setTarget( switched ) );
			}
		}

		return switched;
	}

	private static class TypeVariableResolution {
		private TypeVariableDetails details;

		public void setDetails(TypeVariableDetails details) {
			this.details = details;
		}

		public TypeVariableDetails getDetails() {
			return details;
		}
	}
}
