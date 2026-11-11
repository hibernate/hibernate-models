/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

/**
 * Models a class type.
 *
 * @see Class
 *
 * @author Steve Ebersole
 */
public interface ClassTypeDetails extends ClassBasedTypeDetails {

	@Nonnull
	@Override
	default ClassTypeDetails asClassType() {
		return this;
	}

}
