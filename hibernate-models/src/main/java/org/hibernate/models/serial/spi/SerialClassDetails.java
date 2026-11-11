/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import java.io.Serializable;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;

/**
 * @author Steve Ebersole
 */
public interface SerialClassDetails extends Serializable {
	@Nonnull
	String getName();

	@Nullable
	String getClassName();

	@Nonnull
	ClassDetails toClassDetails(@Nonnull ModelsContext context);
}
