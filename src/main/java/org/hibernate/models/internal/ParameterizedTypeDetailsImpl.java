/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.util.List;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.TypeVariableScope;

import static org.hibernate.models.spi.TypeDetailsHelper.resolveTypeVariableFromParameterizedType;

/**
 * @author Steve Ebersole
 */
public class ParameterizedTypeDetailsImpl implements ParameterizedTypeDetails {
	private final ClassDetails genericClassDetails;
	private final List<TypeDetails> arguments;
	private final TypeVariableScope owner;

	public ParameterizedTypeDetailsImpl(ClassDetails genericClassDetails, List<TypeDetails> arguments, TypeVariableScope owner) {
		this.genericClassDetails = genericClassDetails;
		this.arguments = arguments;
		this.owner = owner;
	}

	@Override
	public ClassDetails getRawClassDetails() {
		return genericClassDetails;
	}

	@Override
	public List<TypeDetails> getArguments() {
		return arguments;
	}

	@Override
	public TypeVariableScope getOwner() {
		return owner;
	}

	@Override
	public TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable) {
		return resolveTypeVariableFromParameterizedType( this, typeVariable );
	}
}
