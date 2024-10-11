/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.io.Serializable;

import org.hibernate.models.spi.SourceModelBuildingContext;

public interface SerialForm<T> extends Serializable {
	T fromSerialForm(SourceModelBuildingContext context);
}
