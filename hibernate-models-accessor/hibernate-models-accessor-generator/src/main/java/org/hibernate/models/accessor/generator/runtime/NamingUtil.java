/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.runtime;

import java.lang.invoke.MethodType;

public final class NamingUtil {

	private NamingUtil() {
	}

	public static <T> String constructorDescriptor(java.lang.reflect.Constructor<T> constructor) {
		MethodType mt = MethodType.methodType( void.class, constructor.getParameterTypes() );
		return mt.toMethodDescriptorString();
	}
}
