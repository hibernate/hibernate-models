/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import org.hibernate.models.spi.ClassDetails;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * @author Steve Ebersole
 */
public interface SerialClassDetails extends StorableForm<ClassDetails> {
	@Nonnull
	String getName();

	@Nullable
	String getClassName();
}
