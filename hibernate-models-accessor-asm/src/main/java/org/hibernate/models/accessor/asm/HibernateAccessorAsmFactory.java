/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.asm;

import org.hibernate.models.accessor.HibernateAccessorFactory;

import java.lang.invoke.MethodHandles;

/**
 * Entry point for the ASM-based accessor strategy.
 *
 * <p>Creates a factory that generates one bulk accessor class per entity at runtime
 * using ASM bytecode generation with {@code TABLESWITCH} dispatch on field/method index.
 */
public final class HibernateAccessorAsmFactory {

	private HibernateAccessorAsmFactory() {
	}

	/**
	 * Creates an ASM-based accessor factory using the given lookup for access control.
	 *
	 * @param lookup the lookup object that determines access rights
	 * @return a new ASM-based factory instance
	 */
	public static HibernateAccessorFactory factory(MethodHandles.Lookup lookup) {
		return new org.hibernate.models.accessor.asm.impl.HibernateAccessorAsmFactory(lookup);
	}
}
