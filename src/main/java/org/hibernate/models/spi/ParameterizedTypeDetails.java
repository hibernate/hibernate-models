/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.util.List;

/**
 * Models a parameterized type.
 * <p/>
 * Given the parameterized type {@code Map<String, Integer>} we'd have<ol>
 *     <li>the {@linkplain #getGenericClassDetails() generic class} {@code Map}
 *     <li>2 {@linkplain #getArguments arguments} - {@code ClassTypeDetails(String)}, {@code ClassTypeDetails(Integer)}.
 * </ol>
 *
 * @see java.lang.reflect.ParameterizedType
 * @see org.jboss.jandex.ParameterizedType
 *
 * @author Steve Ebersole
 */
public interface ParameterizedTypeDetails extends TypeDetails {
	ClassDetails getGenericClassDetails();

	List<TypeDetails> getArguments();

	TypeDetails getOwner();

	@Override
	default Kind getTypeKind() {
		return Kind.PARAMETERIZED_TYPE;
	}

	@Override
	default ParameterizedTypeDetails asParameterizedType() {
		return this;
	}

	@Override
	default String getName() {
		return getGenericClassDetails().getName();
	}

	@Override
	default boolean isImplementor(Class<?> checkType) {
		return getGenericClassDetails().isImplementor( checkType );
	}
}
