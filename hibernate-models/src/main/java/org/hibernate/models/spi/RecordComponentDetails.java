/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Member;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.rendering.internal.RenderingHelper;

import static org.hibernate.models.spi.AnnotationTarget.Kind.RECORD_COMPONENT;

/**
 * Models a {@linkplain java.lang.reflect.RecordComponent component} in a {@linkplain ClassDetails record}
 *
 * @author Steve Ebersole
 */
public interface RecordComponentDetails extends MemberDetails {
	/**
	 * Render this record component and its directly associated annotations.
	 * Contained annotation values are rendered recursively.
	 */
	@Nonnull
	default String render(@Nonnull ModelsContext modelsContext) {
		return RenderingHelper.renderRecordComponent( this, modelsContext );
	}

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

	/**
	 * Provide forward navigational support to a Record's backing field, such that consumers of this API do
	 * not have to correlate it manually through name-matching.
	 * @return The {@link FieldDetails) object that represents a Record's internal backing field
	 * @apiNote implementors can override this default implementation if they have access to a more efficient way of
	 * obtaining the required FieldDetails.
	 */
	@Nullable
	default FieldDetails getField() {
		return getDeclaringType().findFieldByName( getName() );
	}

	/**
	 * Provide forward navigational support to the Record's backing field accessor method, such that consumers of this API do
	 * not have to correlate it manually through method signature matching.
	 * @return The {@link MethodDetails) object that represents the Record's internal field accessor
	 * @apiNote implementors can override this default implementation if they have access to a more efficient way of
	 * obtaining the required MethodDetails.
	 */
	@Nullable
	default MethodDetails getAccessor() {
		for ( MethodDetails method : getDeclaringType().getMethods() ) {
			if ( method.getName().equals( getName() ) && method.getArgumentTypes().isEmpty() ) {
				return method;
			}
		}
		return null;
	}

}
