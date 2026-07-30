/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.spi.HibernateAccessorConfiguration;

import java.lang.invoke.MethodHandles;

/**
 * Entry point for the ASM-based accessor strategy.
 *
 * <p>Creates a factory that generates one bulk accessor class per entity at runtime
 * using ASM bytecode generation with {@code TABLESWITCH} dispatch on field/method index.
 */
public interface HibernateAccessorAsmFactory extends HibernateAccessorFactory {

	/**
	 * Creates an ASM-based accessor factory using the given lookup for access control.
	 *
	 * @param lookup the lookup object that determines access rights
	 * @return a new ASM-based factory instance
	 */
	static HibernateAccessorAsmFactory factory(MethodHandles.Lookup lookup) {
		return factory( new HibernateAccessorConfiguration( lookup ) );
	}

	/**
	 * Creates an ASM-based accessor factory using the given configuration.
	 *
	 * @param configuration the accessor configuration (must contain a {@link HibernateAccessorConfiguration#LOOKUP lookup})
	 * @return a new ASM-based factory instance
	 */
	static HibernateAccessorAsmFactory factory(HibernateAccessorConfiguration configuration) {
		return new org.hibernate.models.accessor.asm.impl.HibernateAccessorAsmFactory( configuration );
	}
}
