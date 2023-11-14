/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.reflect.Modifier;

import org.hibernate.models.spi.MemberDetails;

/**
 * Fills-in non-public aspects of the {@link Modifier} class
 *
 * @author Steve Ebersole
 */
public class ModifierUtils {

	private static final int BRIDGE    = 0x00000040;
	private static final int ENUM      = 0x00004000;
	private static final int SYNTHETIC = 0x00001000;

	/**
	 * Disallow instantiation.  This is a utility class, use statically.
	 */
	private ModifierUtils() {
	}

	public static boolean isTransient(int modifierFlags) {
		return Modifier.isTransient( modifierFlags );
	}

	public static boolean isStatic(int modifierFlags) {
		return Modifier.isStatic( modifierFlags );
	}

	/**
	 * Determine if a method is a bridge based on its modifier flags.
	 *
	 * @return {@code true} if the modifier flags indicate a bridge; {@code false} otherwise.
	 */
	public static boolean isBridge(int modifierFlags) {
		return (modifierFlags & BRIDGE) != 0;
	}

	/**
	 * Determine if a Java type is an enum based on its modifier flags.
	 *
	 * @return {@code true} if the flags indicate an enum; {@code false} otherwise.
	 */
	public static boolean isEnum(int modifierFlags) {
		return (modifierFlags & ENUM) != 0;
	}

	/**
	 * Determine is the given member is synthetic based on its modifier flags
	 *
	 * @return {@code true} if the flags indicate synthetic; {@code false} otherwise.
	 */
	public static boolean isSynthetic(int modifierFlags) {
		return (modifierFlags & SYNTHETIC) != 0;
	}

	/**
	 * Determine if the modifier flags from a field indicate persist-ability.
	 *
	 * @apiNote This is NOT the same as determining whether a field is actually a
	 * persistent attribute.  It is simply checking high-level red-flags (static,
	 * transient, etc.) that indicate it cannot be.
	 *
	 * @see MemberDetails#isPersistable()
	 */
	public static boolean isPersistableField(int modifierFlags) {
		if ( isTransient( modifierFlags ) ) {
			return false;
		}

		if ( ModifierUtils.isSynthetic( modifierFlags ) ) {
			return false;
		}

		return true;
	}

	/**
	 * Determine if the modifier flags from a method indicate persist-ability.
	 *
	 * @apiNote This is NOT the same as determining whether a method is actually a
	 * persistent attribute.  It is simply checking high-level red-flags (static,
	 * transient, etc.) that indicate it cannot be.
	 *
	 * @see MemberDetails#isPersistable()
	 */
	public static boolean isPersistableMethod(int modifierFlags) {
		if ( ModifierUtils.isStatic( modifierFlags ) ) {
			return false;
		}

		if ( ModifierUtils.isBridge( modifierFlags ) ) {
			return false;
		}

		if ( isTransient( modifierFlags ) ) {
			return false;
		}

		if ( ModifierUtils.isSynthetic( modifierFlags ) ) {
			return false;
		}

		return true;
	}
}
