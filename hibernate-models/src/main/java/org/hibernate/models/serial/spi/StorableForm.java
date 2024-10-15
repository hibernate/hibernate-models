/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import java.io.Serializable;

import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Serial form for various parts of a {@linkplain SourceModelBuildingContext context}
 * included in its {@linkplain StorableContext serial form}.
 *
 * @author Steve Ebersole
 */
public interface StorableForm<T> extends Serializable {
	T fromStorableForm(SourceModelBuildingContext context);
}
