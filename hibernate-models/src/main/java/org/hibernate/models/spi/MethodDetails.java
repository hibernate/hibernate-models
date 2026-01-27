/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.util.StringHelper;

import static org.hibernate.models.internal.ModifierUtils.hasPersistableMethodModifiers;

/**
 * Models a {@linkplain java.lang.reflect.Method method} in a {@linkplain ClassDetails class}.
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
	default boolean isPersistable() {
		return getMethodKind() == MethodKind.GETTER
				&& hasPersistableMethodModifiers( getModifiers() );
	}

	@Override
	default String resolveAttributeName() {
		final String methodName = getName();

		if ( methodName.startsWith( "is" ) ) {
			return StringHelper.decapitalize( methodName.substring( 2 ) );
		}
		else if ( methodName.startsWith( "get" ) ) {
			return StringHelper.decapitalize( methodName.substring( 3 ) );
		}

		return null;
	}

	@Override
	Method toJavaMember();

	@Override
	Method toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext);

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast to FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		return this;
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast to RecordComponentDetails" );
	}

}
