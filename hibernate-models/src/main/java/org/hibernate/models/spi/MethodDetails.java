/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.models.internal.util.StringHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import static org.hibernate.models.internal.ModifierUtils.hasPersistableMethodModifiers;

/**
 * Models a {@linkplain java.lang.reflect.Method method} in a {@linkplain ClassDetails class}.
 *
 * @author Steve Ebersole
 */
public interface MethodDetails extends MemberDetails {
	enum MethodKind {
		@Nonnull
		GETTER,
		@Nonnull
		SETTER,
		@Nonnull
		OTHER
	}

	@Nonnull
	MethodKind getMethodKind();

	@Nonnull
	@Override
	default Kind getKind() {
		return Kind.METHOD;
	}

	@Nonnull
	ClassDetails getReturnType();

	@Nonnull
	List<ClassDetails> getArgumentTypes();

	@Override
	default boolean isPersistable() {
		return getMethodKind() == MethodKind.GETTER
				&& hasPersistableMethodModifiers( getModifiers() );
	}

	@Nullable
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

	@Nullable
	@Override
	Method toJavaMember();

	@Nullable
	@Override
	Method toJavaMember(@Nonnull Class<?> declaringClass, @Nonnull ClassLoading classLoading, @Nonnull ModelsContext modelContext);

	@Nonnull
	@Override
	default MethodDetails asMethodDetails() {
		return this;
	}

}
