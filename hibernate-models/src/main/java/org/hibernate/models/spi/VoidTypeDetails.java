/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Models Java's {@code void} (or {@linkplain Void}) type
 *
 * @author Steve Ebersole
 */
public interface VoidTypeDetails extends ClassBasedTypeDetails {
	@Override
	VoidTypeDetails asVoidType();
}
