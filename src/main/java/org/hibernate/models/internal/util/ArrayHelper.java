/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.util;

/**
 * @author Steve Ebersole
 */
public class ArrayHelper {
	public static boolean isEmpty(Object[] array) {
		return array == null || array.length == 0;
	}
}
