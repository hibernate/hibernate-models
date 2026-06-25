/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Field;

import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
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
		return ModifierUtils.hasPersistableFieldModifiers( getModifiers() );
	}

	@Override
	Field toJavaMember();

	@Override
	Field toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext);

	@Override
	default HibernateAccessorValueReader<?> createValueReader() {
		return getModelContext().getAccessorFactory().valueReader( toJavaMember() );
	}

	@Override
	default HibernateAccessorValueWriter createValueWriter() {
		return getModelContext().getAccessorFactory().valueWriter( toJavaMember() );
	}

	@Override
	default FieldDetails asFieldDetails() {
		return this;
	}
}
