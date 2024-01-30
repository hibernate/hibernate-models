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
	 * Find the identified type variable for this type.
	 * <p/>
	 * Foe example, given
	 * <pre class="brush:java">
	 * class {@code Thing<I extends Number>} {
	 *     I id;
	 * }
	 * </pre>
	 * A call to this method with {@code "I"} will return the {@code I extends Number}
	 * type variable.
	 *
	 * @param identifier The type variable identifier
	 *
	 * @return The type variable, or {@code null} none could be found
	 */
	TypeDetails resolveTypeVariable(String identifier);
}
