/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Member;

import org.hibernate.models.IllegalCastException;

import static org.hibernate.models.spi.AnnotationTarget.Kind.RECORD_COMPONENT;

/**
 * Models a {@linkplain java.lang.reflect.RecordComponent component} in a {@linkplain ClassDetails record}
 *
 * @author Steve Ebersole
 */
public interface RecordComponentDetails extends MemberDetails {
	@Override
	default Kind getKind() {
		return RECORD_COMPONENT;
	}

	@Override
	default String resolveAttributeName() {
		return getName();
	}

	@Override
	default boolean isPersistable() {
		return true;
	}

	@Override
	Member toJavaMember();

	@Override
	Member toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext);

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast to FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast to MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		return this;
	}

}
