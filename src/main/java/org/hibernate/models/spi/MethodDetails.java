/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.beans.Introspector;
import java.util.List;

import org.hibernate.models.ModelsException;

/**
 * Models a "{@linkplain java.lang.reflect.Method method}" in a {@link ClassDetails}
 *
 * @author Steve Ebersole
 */
public interface MethodDetails extends MemberDetails {
	enum MethodKind {
		GETTER,
		SETTER,
		OTHER
	}

	MethodKind getMethodKind();

	@Override
	default Kind getKind() {
		return Kind.METHOD;
	}

	ClassDetails getReturnType();
	List<ClassDetails> getArgumentTypes();

	@Override
	default String resolveAttributeName() {
		final String methodName = getName();

		if ( methodName.startsWith( "is" ) ) {
			return Introspector.decapitalize( methodName.substring( 2 ) );
		}
		else if ( methodName.startsWith( "has" ) ) {
			return Introspector.decapitalize( methodName.substring( 3 ) );
		}
		else if ( methodName.startsWith( "get" ) ) {
			return Introspector.decapitalize( methodName.substring( 3 ) );
		}

		throw new ModelsException( "Could not determine attribute name from method - " + methodName );
	}
}
