/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeDetailsHelper;

/**
 * Compatibility bridge for collection element type extraction.
 *
 * @deprecated Use {@link TypeDetailsHelper#extractCollectionElementType(TypeDetails)}.
 */
@Deprecated(since = "1.1", forRemoval = true)
public class CollectionElementSwitch {
	private CollectionElementSwitch() {
	}

	public static TypeDetails extractCollectionElementType(TypeDetails memberType) {
		return TypeDetailsHelper.extractCollectionElementType( memberType );
	}
}
