/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Member;

import org.hibernate.models.internal.ModifierUtils;

/**
 * Models a {@linkplain Member member} in a {@linkplain ClassDetails class} while processing annotations.
 *
 * @apiNote This can be a virtual member, meaning there is no physical member in the declaring type
 * (which itself might be virtual)
 *
 * @author Steve Ebersole
 */
public interface MemberDetails extends AnnotationTarget {
	/**
	 * The name of the member.  This would be the name of the method or field.
	 */
	String getName();

	/**
	 * @return Returns one of:<ul>
	 *     <li>for a field, the field type</li>
	 *     <li>for a getter method, the return type</li>
	 *     <li>for a setter method, the argument type</li>
	 *     <li>otherwise, {@code null}</li>
	 * </ul>
	 */
	ClassDetails getType();

	/**
	 * Whether the {@linkplain #getType() type} of this member, if one, is
	 * considered {@linkplain ClassDetails#isResolved() resolved}.
	 *
	 * @return {@code true} when there is a type for this member, and that type is resolved; {@code false} otherwise.
	 */
	default boolean isTypeResolved() {
		final ClassDetails type = getType();
		if ( type == null ) {
			return false;
		}
		return type.isResolved();
	}

	/**
	 * Access to the member modifier flags.
	 *
	 * @see Member#getModifiers()
	 */
	int getModifiers();

	/**
	 * Get the member's visibility
	 */
	default Visibility getVisibility() {
		return ModifierUtils.resolveVisibility( getModifiers() );
	}

	/**
	 * Whether the member is {@linkplain ModifierUtils#isStatic static}
	 */
	default boolean isStatic() {
		return ModifierUtils.isStatic( getModifiers() );
	}

	/**
	 * Whether the member is {@linkplain ModifierUtils#isSynthetic synthetic}
	 */
	default boolean isSynthetic() {
		return ModifierUtils.isSynthetic( getModifiers() );
	}

	default boolean isFinal() {
		return ModifierUtils.isFinal( getModifiers() );
	}

	/**
	 * Whether the member is a field.
	 *
	 * @return {@code true} indicates the member is a field; {@code false} indicates it is a method.
	 */
	default boolean isField() {
		return getKind() == Kind.FIELD;
	}

	/**
	 * Can this member be a persistent attribute
	 */
	boolean isPersistable();

	/**
	 * For members potentially representing attributes based on naming patterns, determine the attribute name.
	 * Return {@code null} if the name does not match the pattern for an attribute name.
	 *
	 * @return The potential attribute name, or {@code null}.
	 *
	 * @apiNote For a {@linkplain Kind#FIELD field}, this will be the name of the field;
	 * for a {@linkplain Kind#METHOD method}, this will be the name as determined by the
	 * Java Bean naming pattern.
	 */
	String resolveAttributeName();

	enum Visibility {
		PUBLIC,
		PROTECTED,
		PACKAGE,
		PRIVATE
	}
}
