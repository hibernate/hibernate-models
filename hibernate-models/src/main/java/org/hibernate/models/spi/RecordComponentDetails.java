/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Member;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import static org.hibernate.models.spi.AnnotationTarget.Kind.RECORD_COMPONENT;

/**
 * Models a {@linkplain java.lang.reflect.RecordComponent component} in a {@linkplain ClassDetails record}
 *
 * @author Steve Ebersole
 */
public interface RecordComponentDetails extends MemberDetails {
	@Nonnull
	@Override
	default Kind getKind() {
		return RECORD_COMPONENT;
	}

	@Nonnull
	@Override
	default String resolveAttributeName() {
		return getName();
	}

	@Override
	default boolean isPersistable() {
		return true;
	}

	@Nullable
	@Override
	Member toJavaMember();

	@Nullable
	@Override
	Member toJavaMember(@Nonnull Class<?> declaringClass, @Nonnull ClassLoading classLoading, @Nonnull ModelsContext modelContext);

	@Nonnull
	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		return this;
	}

}
