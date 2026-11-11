/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Field;

import org.hibernate.models.internal.ModifierUtils;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Models a {@linkplain java.lang.reflect.Field field} in a {@linkplain ClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface FieldDetails extends MemberDetails {
	@Nonnull
	@Override
	default Kind getKind() {
		return Kind.FIELD;
	}

	@Nonnull
	@Override
	default String resolveAttributeName() {
		return getName();
	}

	@Override
	default boolean isPersistable() {
		return ModifierUtils.hasPersistableFieldModifiers( getModifiers() );
	}

	@Nullable
	@Override
	Field toJavaMember();

	@Nullable
	@Override
	Field toJavaMember(@Nonnull Class<?> declaringClass, @Nonnull ClassLoading classLoading, @Nonnull ModelsContext modelContext);

	@Nonnull
	@Override
	default FieldDetails asFieldDetails() {
		return this;
	}
}
