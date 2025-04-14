/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import org.hibernate.models.spi.ModelsContext;

/**
 * A part of the {@linkplain ModelsContext model context} which can
 * be stored in the context's {@linkplain StorableContext serial form}.
 *
 * @param <T> The storable's type
 * @param <S> The storable's serial-type's type
 */
public interface Storable<T,S extends StorableForm<T>> {
	S toStorableForm();
}
