/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.TypeVariableReferenceDetails;

/**
 * @author Steve Ebersole
 */
public class TypeVariableReferenceDetailsImpl implements TypeVariableReferenceDetails {
	private final String identifier;
	private TypeVariableDetails target;

	public TypeVariableReferenceDetailsImpl(String identifier) {
		this.identifier = identifier;
	}

	public TypeVariableReferenceDetailsImpl(String identifier, TypeVariableDetails target) {
		this.identifier = identifier;
		this.target = target;
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public TypeVariableDetails getTarget() {
		if (target == null) {
			throw new IllegalStateException("Type variable reference " + identifier + " was not patched correctly");
		}
		return target;
	}

	public void setTarget(TypeVariableDetails target) {
		if ( target == null ) {
			throw new IllegalArgumentException("Type variable reference target must not be null");
		}
		this.target = target;
	}

	@Override
	public String getName() {
		if ( target == null ) {
			throw new IllegalStateException("Type variable reference " + identifier + " was not patched correctly");
		}
		return target.getName();
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		return getTarget().isImplementor( checkType );
	}

	@Override
	public TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable) {
		return this.identifier.equals( typeVariable.getIdentifier() ) ? target : null;
	}
}
