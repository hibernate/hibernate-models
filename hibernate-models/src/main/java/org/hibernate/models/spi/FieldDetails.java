/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Field;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.rendering.internal.RenderingHelper;

/**
 * Models a {@linkplain java.lang.reflect.Field field} in a {@linkplain ClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface FieldDetails extends MemberDetails {
	/**
	 * Render this field and its directly associated annotations.  Contained
	 * annotation values are rendered recursively.
	 */
	@Nonnull
	default String render(@Nonnull ModelsContext modelsContext) {
		return RenderingHelper.renderField( this, modelsContext );
	}

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

	/**
	 * Provides reverse navigational access to this {@link FieldDetails}' owning {@link RecordComponentDetails}.
	 * @return the RecordComponentDetails object that owns this FieldDetails.
	 */
	@Nullable
	default RecordComponentDetails getCorrespondingRecordComponent() {
		if ( !getDeclaringType().isRecord() ) {
			return null;
		}
		return getDeclaringType().findRecordComponentByName( getName() );
	}

}
