/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;

/**
 * Compatibility bridge for map value type extraction.
 *
 * @deprecated Use {@link TypeDetailsHelper#extractMapValueType(TypeDetails)}.
 */
@Deprecated(since = "1.1", forRemoval = true)
public class MapValueSwitch {
	private MapValueSwitch() {
	}

	public static TypeDetails extractMapValueType(TypeDetails memberType) {
		return TypeDetailsHelper.extractMapValueType( memberType );
	}
}
