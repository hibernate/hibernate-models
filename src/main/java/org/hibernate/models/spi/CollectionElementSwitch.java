/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.util.Collection;

import org.hibernate.models.internal.TypeDetailsSwitchSupport;
import org.hibernate.models.internal.TypeDetailsSwitcher;

/**
 * Used to determine the type details for a Collection element - see {@linkplain #extractCollectionElementType(TypeDetails)}
 *
 * @author Steve Ebersole
 */
public class CollectionElementSwitch extends TypeDetailsSwitchSupport<TypeDetails> {

	public static TypeDetails extractCollectionElementType(TypeDetails memberType) {
		assert memberType.isImplementor( Collection.class );

		// we may need to handle "concrete types" such as `class SpecialList implements List<String>`
		final ClassDetails rawClassDetails = memberType.determineRawClass();
		final CollectionElementSwitch collectionElementSwitch = new CollectionElementSwitch( memberType );

		// first, check super-type...
		if ( rawClassDetails.getGenericSuperType() != null ) {
			final TypeDetails typeDetails = TypeDetailsSwitcher.switchType( rawClassDetails.getGenericSuperType(), collectionElementSwitch );
			if ( typeDetails != null ) {
				return typeDetails;
			}
		}

		// then, interfaces...
		for ( TypeDetails implementedInterface : rawClassDetails.getImplementedInterfaces() ) {
			final TypeDetails typeDetails = TypeDetailsSwitcher.switchType( implementedInterface, collectionElementSwitch );
			if ( typeDetails != null ) {
				return typeDetails;
			}
		}

		// otherwise, assume Object
		return ClassBasedTypeDetails.OBJECT_TYPE_DETAILS;
	}

	private final TypeDetails memberTypeDetails;

	private CollectionElementSwitch(TypeDetails memberTypeDetails) {
		this.memberTypeDetails = memberTypeDetails;
	}

	@Override
	public TypeDetails caseClass(ClassTypeDetails classType) {
		if ( classType.isImplementor( Collection.class ) ) {
			if ( classType.getClassDetails().getGenericSuperType() != null ) {
				return TypeDetailsSwitcher.switchType( classType.getClassDetails().getGenericSuperType(), this );
			}
		}
		return null;
	}

	@Override
	public TypeDetails caseParameterizedType(ParameterizedTypeDetails parameterizedType) {
		if ( parameterizedType.isImplementor( Collection.class ) ) {
			return parameterizedType.getArguments().get( 0 );
		}
		return null;
	}

	@Override
	public TypeDetails caseWildcardType(WildcardTypeDetails wildcardType) {
		if ( wildcardType.isImplementor( Collection.class ) ) {
			return wildcardType.getBound();
		}
		return null;
	}

	@Override
	public TypeDetails caseTypeVariable(TypeVariableDetails typeVariable) {
		if ( typeVariable.isImplementor( Collection.class ) ) {
			return memberTypeDetails.resolveTypeVariable( typeVariable.getIdentifier() );
		}
		return null;
	}

	@Override
	public TypeDetails caseTypeVariableReference(TypeVariableReferenceDetails typeVariableReference) {
		if ( typeVariableReference.isImplementor( Collection.class ) ) {
			return memberTypeDetails.resolveTypeVariable( typeVariableReference.getIdentifier() );
		}
		return null;
	}
}
