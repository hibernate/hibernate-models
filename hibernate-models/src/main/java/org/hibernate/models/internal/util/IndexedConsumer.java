/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.util;

/**
 * A consumer, like {@link java.util.function.Consumer}, accepting a value and its index.
 *
 * @author Christian Beikov
 * @author Steve Ebersole
 */
@FunctionalInterface
public interface IndexedConsumer<T> {
	void accept(int index, T t);
}
