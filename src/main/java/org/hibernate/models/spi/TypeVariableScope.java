/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * A scope for {@linkplain TypeVariableDetails} references
 *
 * @author Steve Ebersole
 */
public interface TypeVariableScope {
	/**
	 * Resolve the type of the provided type variable relative to this scope.
	 * <p/>
	 * For example, given
	 * <pre class="brush:java">
	 * class {@code Thing<I extends Number>} {
	 *     I id;
	 * }
	 * </pre>
	 * A call to this method on the {@code Thing} scope with the type variable representing
	 * {@code I} will return the {@code I extends Number} type variable definition itself.
	 * <p/>
	 * If this scope defines a corresponding type argument, the concrete type is returned.
	 * For example, given
	 * <pre class="brush:java">
	 * class {@code Stuff extends Thing<Integer>} {
	 * }
	 * </pre>
	 * This method will yield the {@code Integer} type details.
	 *
	 * @param typeVariable The type variable to resolve
	 *
	 * @return The type variable's resolved type, or {@code null} none could be found
	 */
	TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable);

	/**
	 * Determine the raw {@linkplain ClassDetails class} for the given type. Never returns {@code null}, opting
	 * to return {@linkplain ClassDetails#OBJECT_CLASS_DETAILS Object} instead if the raw class is not known
	 *
	 * @return The raw class details, or {@linkplain ClassDetails#OBJECT_CLASS_DETAILS Object} if "not known".
	 */
	ClassDetails determineRawClass();
}
