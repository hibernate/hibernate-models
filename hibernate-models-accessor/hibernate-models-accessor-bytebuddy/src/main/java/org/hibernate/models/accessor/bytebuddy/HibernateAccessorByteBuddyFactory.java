/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.bytebuddy;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.bytebuddy.spi.MultiValueAccessorPointcuts;
import org.hibernate.models.accessor.spi.HibernateAccessorConfiguration;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Member;

/**
 * Entry point for the ByteBuddy-based accessor strategy.
 *
 * <p>Creates a factory that generates one bulk accessor class per entity at runtime
 * using ByteBuddy's shaded ASM bytecode generation with {@code TABLESWITCH} dispatch
 * on field/method index.
 */
public interface HibernateAccessorByteBuddyFactory extends HibernateAccessorFactory {

	/**
	 * Creates a ByteBuddy-based accessor factory using the given lookup for access control.
	 *
	 * @param lookup the lookup object that determines access rights
	 * @return a new ByteBuddy-based factory instance
	 */
	static HibernateAccessorByteBuddyFactory factory(MethodHandles.Lookup lookup) {
		return factory( new HibernateAccessorConfiguration( lookup ) );
	}

	/**
	 * Creates a ByteBuddy-based accessor factory using the given configuration.
	 *
	 * @param configuration the accessor configuration (must contain a {@link HibernateAccessorConfiguration#LOOKUP lookup})
	 * @return a new ByteBuddy-based factory instance
	 */
	static HibernateAccessorByteBuddyFactory factory(HibernateAccessorConfiguration configuration) {
		return new org.hibernate.models.accessor.bytebuddy.impl.HibernateAccessorByteBuddyFactory( configuration );
	}

	HibernateAccessorMultiValueReader multiValueReader(
			Class<?> declaringClass,
			Member[] members,
			MultiValueAccessorPointcuts pointcuts);

	HibernateAccessorMultiValueWriter multiValueWriter(
			Class<?> declaringClass,
			Member[] members,
			MultiValueAccessorPointcuts pointcuts);
}
