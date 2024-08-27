/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * Models a reference to a type variable in the bound of a recursive type parameter.
 * <p/>
 * The referenced {@linkplain TypeVariableDetails type variable} is available via
 * {@linkplain #getTarget()}. The {@linkplain #getName() name} of the type variable
 * corresponds to the raw type name which here comes from the {@linkplain #getTarget() target}.
 * The {@linkplain #getIdentifier() identifier}  is the name of the type variable as present
 * in the source code.
 * <p/>
 * For example,
 * <pre class="brush:java">
 * {@code T extends Comparable<T>}
 * </pre>
 * In this case, we have uses of the 2 type variable {@code T}.  The initial one
 * is modeled as a {@linkplain TypeVariableDetails}.  The second one (as part of
 * {@code Comparable<T>}) is modeled as a {@linkplain TypeVariableReferenceDetails}.
 * The {@linkplain #getIdentifier() identifier} is {@code T} while the {@linkplain #getName() name}
 * is {@code Comparable}.
 *
 * @apiNote This split between {@linkplain TypeVariableDetails} and {@linkplain TypeVariableReferenceDetails}
 * helps protect against stack overflows while processing generics.
 *
 * @author Steve Ebersole
 */
public interface TypeVariableReferenceDetails extends TypeDetails {
	String getIdentifier();

	TypeVariableDetails getTarget();

	@Override
	default Kind getTypeKind() {
		return Kind.TYPE_VARIABLE_REFERENCE;
	}

	@Override
	default TypeVariableReferenceDetails asTypeVariableReference() {
		return this;
	}
}
