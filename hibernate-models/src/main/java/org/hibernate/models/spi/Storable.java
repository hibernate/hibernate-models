/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import java.io.IOException;

import org.hibernate.models.internal.SerialForm;

public interface Storable<T,S extends SerialForm<T>> {
	S toSerialForm(SourceModelBuildingContext context);
}
