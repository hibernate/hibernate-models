/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Models a "{@linkplain java.lang.reflect.Field field}" in a {@link ClassDetails}
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
}
