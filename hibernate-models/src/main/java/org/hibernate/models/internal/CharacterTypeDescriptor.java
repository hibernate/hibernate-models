/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Descriptor for char values
 *
 * @author Steve Ebersole
 */
public class CharacterTypeDescriptor extends AbstractTypeDescriptor<Character> {
	public static final CharacterTypeDescriptor CHARACTER_TYPE_DESCRIPTOR = new CharacterTypeDescriptor();

	@Override
	public Class<Character> getValueType() {
		return Character.class;
	}

	@Override
	public Object unwrap(Character value) {
		return value;
	}

	@Override
	public Character[] makeArray(int size, SourceModelBuildingContext modelContext) {
		return new Character[size];
	}
}
