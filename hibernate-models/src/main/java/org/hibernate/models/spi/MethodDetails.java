/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Method;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.rendering.internal.RenderingHelper;

import static org.hibernate.models.internal.ModifierUtils.hasPersistableMethodModifiers;

/**
 * Models a {@linkplain java.lang.reflect.Method method} in a {@linkplain ClassDetails class}.
 *
 * @author Steve Ebersole
 */
public interface MethodDetails extends MemberDetails {
	/**
	 * Render this method and its directly associated annotations.  Contained
	 * annotation values are rendered recursively.
	 */
	@Nonnull
	default String render(@Nonnull ModelsContext modelsContext) {
		return RenderingHelper.renderMethod( this, modelsContext );
	}

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

	/**
	 * Provides reverse navigational access to this {@link MethodDetails}' owning {@link RecordComponentDetails}.
	 * @return the RecordComponentDetails object that owns this MethodDetails.
	 */
	@Nullable
	default RecordComponentDetails getCorrespondingRecordComponent() {
		if ( !getDeclaringType().isRecord() ) {
			return null;
		}
		// record accessors don't follow the JavaBean get/is naming convention, so JdkBuilders classifies them as MethodKind.OTHER
		if ( !getArgumentTypes().isEmpty() ) {
			return null;
		}
		return getDeclaringType().findRecordComponentByName( getName() );
	}

}
