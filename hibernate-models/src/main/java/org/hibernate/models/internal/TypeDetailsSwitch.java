/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.ArrayTypeDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.PrimitiveTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.TypeVariableReferenceDetails;
import org.hibernate.models.spi.VoidTypeDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

/**
 * Switch-style handling for {@linkplain org.hibernate.models.spi.TypeDetails}
 *
 * @author Steve Ebersole
 */
public interface TypeDetailsSwitch<T> {
	T caseVoid(VoidTypeDetails voidType);

	T casePrimitive(PrimitiveTypeDetails primitiveType);

	T caseClass(ClassTypeDetails classType);

	T caseArrayType(ArrayTypeDetails arrayType);

	T caseParameterizedType(ParameterizedTypeDetails parameterizedType);

	T caseWildcardType(WildcardTypeDetails wildcardType);

	T caseTypeVariable(TypeVariableDetails typeVariable);

	T caseTypeVariableReference(TypeVariableReferenceDetails typeVariableReference);

	T defaultCase(TypeDetails type);
}
