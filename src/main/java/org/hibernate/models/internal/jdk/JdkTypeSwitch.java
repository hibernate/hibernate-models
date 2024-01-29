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

/**
 * @author Steve Ebersole
 */
public interface JdkTypeSwitch<T> {
	T caseClass(Class<?> classType);

	T caseParameterizedType(ParameterizedType parameterizedType);

	T caseWildcardType(WildcardType wildcardType);

	T caseTypeVariable(TypeVariable<?> typeVariable);

	T caseGenericArrayType(GenericArrayType genericArrayType);

	T defaultCase(Type t);
}
