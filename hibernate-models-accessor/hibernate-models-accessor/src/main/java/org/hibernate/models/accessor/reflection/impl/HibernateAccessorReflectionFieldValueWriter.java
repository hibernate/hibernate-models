/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.reflection.impl;

import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.reflect.Field;
import java.util.Objects;

public class HibernateAccessorReflectionFieldValueWriter implements HibernateAccessorValueWriter {

	private final Field field;

	public HibernateAccessorReflectionFieldValueWriter(Field field) {
		this.field = field;
		field.setAccessible(true);
	}

	@Override
	public void set(Object instance, Object value) {
		try {
			field.set(instance, value);
		}
		catch (RuntimeException | IllegalAccessException e) {
			throw CoreLog.INSTANCE.errorInvokingMember(field, Objects.toString(instance), e,
					e.getMessage());
		}
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + field + "]";
	}

	@Override
	public int hashCode() {
		return field.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !obj.getClass().equals(getClass())) {
			return false;
		}
		HibernateAccessorReflectionFieldValueWriter other = (HibernateAccessorReflectionFieldValueWriter) obj;
		return field.equals(other.field);
	}
}
