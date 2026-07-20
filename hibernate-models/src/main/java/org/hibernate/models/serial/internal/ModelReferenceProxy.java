/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.io.Serial;
import java.io.Serializable;

import org.hibernate.models.serial.spi.ModelReference;

/// Serializable proxy written into an owning serialized payload in place of a
/// live Hibernate Models object.
///
/// @author Steve Ebersole
record ModelReferenceProxy(ModelReference reference) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	ModelReferenceProxy {
		if ( reference == null ) {
			throw new IllegalArgumentException( "Model reference cannot be null" );
		}
	}
}
