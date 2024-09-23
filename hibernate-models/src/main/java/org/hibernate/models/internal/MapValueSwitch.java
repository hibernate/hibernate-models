/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.Map;

import org.hibernate.models.spi.ClassBasedTypeDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.TypeVariableReferenceDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

/**
 * Used to determine the type details for a Map value - see {@linkplain #extractMapValueType(TypeDetails)}
 *
 * @author Steve Ebersole
 */
public class MapValueSwitch extends TypeDetailsSwitchSupport<TypeDetails> {

	public static TypeDetails extractMapValueType(TypeDetails memberType) {
		assert memberType.isImplementor( Map.class );

		final ClassDetails rawClassDetails = memberType.determineRawClass();
		final MapValueSwitch mapValueSwitch = new MapValueSwitch( memberType );

		// first, check super-type...
		if ( rawClassDetails.getGenericSuperType() != null ) {
			final TypeDetails typeDetails = TypeDetailsSwitcher.switchType( rawClassDetails.getGenericSuperType(), mapValueSwitch );
			if ( typeDetails != null ) {
				return typeDetails;
			}
		}

		// then, interfaces...
		for ( TypeDetails implementedInterface : rawClassDetails.getImplementedInterfaces() ) {
			final TypeDetails typeDetails = TypeDetailsSwitcher.switchType( implementedInterface, mapValueSwitch );
			if ( typeDetails != null ) {
				return typeDetails;
			}
		}

		// otherwise, assume Object
		return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
	}

	private final TypeDetails memberTypeDetails;

	private MapValueSwitch(TypeDetails memberTypeDetails) {
		this.memberTypeDetails = memberTypeDetails;
	}

	@Override
	public TypeDetails caseClass(ClassTypeDetails classType) {
		if ( classType.isImplementor( Map.class ) ) {
			if ( classType.getClassDetails().getGenericSuperType() != null ) {
				return TypeDetailsSwitcher.switchType( classType.getClassDetails().getGenericSuperType(), this );
			}
		}
		return null;
	}

	@Override
	public TypeDetails caseParameterizedType(ParameterizedTypeDetails parameterizedType) {
		if ( parameterizedType.isImplementor( Map.class ) ) {
			return parameterizedType.getArguments().get( 1 );
		}
		return null;
	}

	@Override
	public TypeDetails caseWildcardType(WildcardTypeDetails wildcardType) {
		if ( wildcardType.isImplementor( Map.class ) ) {
			return wildcardType.getBound();
		}
		return null;
	}

	@Override
	public TypeDetails caseTypeVariable(TypeVariableDetails typeVariable) {
		if ( typeVariable.isImplementor( Map.class ) ) {
			return memberTypeDetails.resolveTypeVariable( typeVariable );
		}
		return null;
	}

	@Override
	public TypeDetails caseTypeVariableReference(TypeVariableReferenceDetails typeVariableReference) {
		if ( typeVariableReference.isImplementor( Map.class ) ) {
			return memberTypeDetails.resolveTypeVariable( typeVariableReference.getTarget() );
		}
		return null;
	}
}
