/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Member;

/**
 * Details about a "{@linkplain Member member}" while processing annotations.
 *
 * @apiNote This can be a virtual member, meaning there is no physical
 * member in the declaring type (which itself might be virtual)
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
	 *     <li>For a field, the field type</li>
	 *     <li>For a getter method, the return type</li>
	 *     <li>For a setter method, the argument type</li>
	 *     <li>{@code null}</li>
	 * </ul>
	 */
	ClassDetails getType();

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
	 * For members representing attributes, determine the attribute name
	 */
	String resolveAttributeName();
}
