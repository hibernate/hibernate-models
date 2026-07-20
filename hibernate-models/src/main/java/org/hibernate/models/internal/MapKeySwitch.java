/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;

/**
 * Compatibility bridge for map key type extraction.
 *
 * @deprecated Use {@link TypeDetailsHelper#extractMapKeyType(TypeDetails)}.
 */
@Deprecated(since = "1.1", forRemoval = true)
public class MapKeySwitch {
	private MapKeySwitch() {
	}

	public static TypeDetails extractMapKeyType(TypeDetails memberType) {
		return TypeDetailsHelper.extractMapKeyType( memberType );
	}
}
