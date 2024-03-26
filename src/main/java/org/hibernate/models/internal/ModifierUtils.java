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

	public static final int BRIDGE    = 0x00000040;
	public static final int SYNTHETIC = 0x00001000;

	public static final int DYNAMIC_ATTRIBUTE_MODIFIERS = ~Modifier.ABSTRACT
			& ~BRIDGE
			& ~Modifier.FINAL
			& ~Modifier.STATIC
			& ~SYNTHETIC
			& ~Modifier.TRANSIENT;

	/**
	 * Disallow instantiation.  This is a utility class, use statically.
	 */
	private ModifierUtils() {
	}

	public static MemberDetails.Visibility resolveVisibility(int modifierFlags) {
		if ( Modifier.isPublic( modifierFlags ) ) {
			return MemberDetails.Visibility.PUBLIC;
		}
		if ( Modifier.isProtected( modifierFlags ) ) {
			return MemberDetails.Visibility.PROTECTED;
		}
		if ( Modifier.isPrivate( modifierFlags ) ) {
			return MemberDetails.Visibility.PRIVATE;
		}
		return MemberDetails.Visibility.PACKAGE;
	}

	public static boolean isTransient(int modifierFlags) {
		return Modifier.isTransient( modifierFlags );
	}

	/**
	 * Whether the {@code modifierFlags} indicates the member is {@linkplain Modifier#isStatic static}
	 */
	public static boolean isStatic(int modifierFlags) {
		return Modifier.isStatic( modifierFlags );
	}

	/**
	 * Whether the {@code modifierFlags} indicates the member is {@linkplain Modifier#isFinal final}
	 */
	public static boolean isFinal(int modifierFlags) {
		return Modifier.isFinal( modifierFlags );
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
	 * Determine if a method is a bridge based on its modifier flags.
	 *
	 * @return {@code true} if the modifier flags indicate a bridge; {@code false} otherwise.
	 */
	public static boolean isBridge(int modifierFlags) {
		return (modifierFlags & BRIDGE) != 0;
	}

	public static boolean isAbstract(int modifierFlags) {
		return (modifierFlags & Modifier.ABSTRACT) != 0;
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
	public static boolean hasPersistableFieldModifiers(int modifierFlags) {
		return !isTransient( modifierFlags )
				&& !isSynthetic( modifierFlags )
				&& !isStatic( modifierFlags );
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
	public static boolean hasPersistableMethodModifiers(int modifierFlags) {
		return !isStatic( modifierFlags )
				&& !isBridge( modifierFlags )
				&& !isTransient( modifierFlags )
				&& !isSynthetic( modifierFlags );
	}
}
