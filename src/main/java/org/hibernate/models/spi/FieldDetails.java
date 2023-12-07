/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import org.hibernate.models.internal.ModifierUtils;

/**
 * Models a {@linkplain java.lang.reflect.Field field} in a {@linkplain ClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface FieldDetails extends MemberDetails {
	@Override
	default Kind getKind() {
		return Kind.FIELD;
	}

	@Override
	default String resolveAttributeName() {
		return getName();
	}

	@Override
	default boolean isPersistable() {
		return ModifierUtils.isPersistableField( getModifiers() );
	}
}
