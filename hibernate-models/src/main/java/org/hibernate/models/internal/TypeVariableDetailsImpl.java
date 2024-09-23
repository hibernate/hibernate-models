/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.List;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

/**
 * @author Steve Ebersole
 */
public class TypeVariableDetailsImpl implements TypeVariableDetails {
	private final String identifier;
	private final ClassDetails declaringType;
	private final List<TypeDetails> bounds;

	private final String name;

	public TypeVariableDetailsImpl(String identifier, ClassDetails declaringType, List<TypeDetails> bounds) {
		this.identifier = identifier;
		this.declaringType = declaringType;
		this.bounds = bounds;

		this.name = calculateName( bounds );
	}

	private String calculateName(List<TypeDetails> bounds) {
		if ( !bounds.isEmpty() ) {
			return bounds.get(0).getName();
		}
		return Object.class.getName();
	}

	@Override public String getIdentifier() {
		return identifier;
	}

	@Override public ClassDetails getDeclaringType() {
		return declaringType;
	}

	@Override public List<TypeDetails> getBounds() {
		return bounds;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		if ( bounds.size() == 1 ) {
			return bounds.get(0).isImplementor( checkType );
		}
		return checkType == Object.class;
	}

	@Override
	public TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable) {
		return identifier.equals( typeVariable.getIdentifier() ) ? this : null;
	}

	@Override
	public String toString() {
		return "TypeVariableDetails(" + identifier + " : " + name + ")";
	}
}
