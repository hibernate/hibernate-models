/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

/**
 * Models Java's {@code void} (or {@linkplain Void}) type
 *
 * @author Steve Ebersole
 */
public interface VoidTypeDetails extends ClassBasedTypeDetails {
	@Nonnull
	@Override
	VoidTypeDetails asVoidType();
}
