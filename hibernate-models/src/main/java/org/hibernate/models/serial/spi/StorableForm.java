/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import jakarta.annotation.Nonnull;

import java.io.Serializable;

import org.hibernate.models.spi.ModelsContext;

/**
 * Serial form for various parts of a {@linkplain ModelsContext context}
 * included in its {@linkplain StorableContext serial form}.
 *
 * @author Steve Ebersole
 */
public interface StorableForm<T> extends Serializable {
	@Nonnull
	T fromStorableForm(@Nonnull ModelsContext context);
}
