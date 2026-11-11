/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Models a parameterized type.
 * <p/>
 * Given the parameterized type {@code Map<String, Integer>} we'd have<ol>
 *     <li>the {@linkplain #getRawClassDetails() raw class} {@code Map}
 *     <li>2 {@linkplain #getArguments arguments} - {@code ClassTypeDetails(String)}, {@code ClassTypeDetails(Integer)}.
 * </ol>
 *
 * @see java.lang.reflect.ParameterizedType
 *
 * @author Steve Ebersole
 */
public interface ParameterizedTypeDetails extends ClassBasedTypeDetails {
	@Nonnull
	ClassDetails getRawClassDetails();

	@Nonnull
	List<TypeDetails> getArguments();

	/**
	 * The owning scope, or {@code null} when no owner is available.
	 */
	@Nullable
	TypeVariableScope getOwner();

	@Nonnull
	@Override
	default ClassDetails getClassDetails() {
		return getRawClassDetails();
	}

	@Nonnull
	@Override
	default Kind getTypeKind() {
		return Kind.PARAMETERIZED_TYPE;
	}

	@Nonnull
	@Override
	default ParameterizedTypeDetails asParameterizedType() {
		return this;
	}

	@Nonnull
	@Override
	default String getName() {
		return getRawClassDetails().getName();
	}

	@Override
	default boolean isImplementor(@Nonnull Class<?> checkType) {
		return getRawClassDetails().isImplementor( checkType );
	}
}
