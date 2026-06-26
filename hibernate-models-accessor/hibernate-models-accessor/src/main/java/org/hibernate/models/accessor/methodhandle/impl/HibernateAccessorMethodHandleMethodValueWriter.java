/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.methodhandle.impl;

import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.logging.impl.CoreLog;

import java.lang.invoke.MethodHandle;

public class HibernateAccessorMethodHandleMethodValueWriter implements HibernateAccessorValueWriter {
	private final MethodHandle target;

	public HibernateAccessorMethodHandleMethodValueWriter(MethodHandle target) {
		this.target = target;
	}

	@Override
	public void set(Object instance, Object value) {
		try {
			target.invoke(instance, value);
		}
		catch (Throwable t) {
			if (t instanceof Error) {
				throw (Error) t;
			}
			throw CoreLog.INSTANCE.errorInvokingHandle(target, String.valueOf(instance), t, t.getMessage());
		}
	}
}
