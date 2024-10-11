/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.dynamic;

import org.hibernate.models.internal.SerialCassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

public class SerialDynamicClassDetails implements SerialCassDetails {
	@Override
	public ClassDetails fromSerialForm(SourceModelBuildingContext context) {
		throw new UnsupportedOperationException( "Not implemented yet" );
	}
}
