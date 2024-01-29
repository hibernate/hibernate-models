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

/**
 * @author Steve Ebersole
 */
public class ParameterizedTypeDetailsImpl implements ParameterizedTypeDetails {
	private final ClassDetails genericClassDetails;
	private final List<TypeDetails> arguments;
	private final TypeDetails owner;

	public ParameterizedTypeDetailsImpl(ClassDetails genericClassDetails, List<TypeDetails> arguments, TypeDetails owner) {
		this.genericClassDetails = genericClassDetails;
		this.arguments = arguments;
		this.owner = owner;
	}

	@Override
	public ClassDetails getGenericClassDetails() {
		return genericClassDetails;
	}

	@Override
	public List<TypeDetails> getArguments() {
		return arguments;
	}

	@Override
	public TypeDetails getOwner() {
		return owner;
	}
}
