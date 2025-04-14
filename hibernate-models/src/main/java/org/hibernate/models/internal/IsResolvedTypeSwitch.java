/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.List;

import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.PrimitiveTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsSwitch;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.TypeVariableReferenceDetails;
import org.hibernate.models.spi.VoidTypeDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

/**
 * TypeDetailsSwitch implementation checking whether a type is resolved (all of its bounds are known)
 *
 * @author Steve Ebersole
 */
public class IsResolvedTypeSwitch implements TypeDetailsSwitch<Boolean> {
	public static final IsResolvedTypeSwitch IS_RESOLVED_SWITCH = new IsResolvedTypeSwitch();

	@Override
	public Boolean caseClass(ClassTypeDetails classType, ModelsContext modelsContext) {
		return true;
	}

	@Override
	public Boolean casePrimitive(PrimitiveTypeDetails primitiveType, ModelsContext modelsContext) {
		return true;
	}

	@Override
	public Boolean caseVoid(VoidTypeDetails voidType, ModelsContext modelsContext) {
		return true;
	}

	@Override
	public Boolean caseArrayType(ArrayTypeDetails arrayType, ModelsContext modelsContext) {
		return arrayType.getConstituentType().isResolved();
	}

	@Override
	public Boolean caseParameterizedType(
			ParameterizedTypeDetails parameterizedType,
			ModelsContext modelsContext) {
		final List<TypeDetails> typeArgs = parameterizedType.getArguments();
		for ( TypeDetails arg : typeArgs ) {
			if ( !arg.isResolved() ) {
				return false;
			}
		}
		return true;
	}

	@Override
	public Boolean caseWildcardType(WildcardTypeDetails wildcardType, ModelsContext modelsContext) {
		final TypeDetails bound = wildcardType.getBound();
		return bound != null && bound.isResolved();
	}

	@Override
	public Boolean caseTypeVariable(TypeVariableDetails typeVariable, ModelsContext modelsContext) {
		return false;
	}

	@Override
	public Boolean caseTypeVariableReference(
			TypeVariableReferenceDetails typeVariableReference,
			ModelsContext modelsContext) {
		return false;
	}

	@Override
	public Boolean defaultCase(TypeDetails type, ModelsContext modelsContext) {
		throw new UnsupportedOperationException( "Unexpected attribute type - " + type );
	}
}
