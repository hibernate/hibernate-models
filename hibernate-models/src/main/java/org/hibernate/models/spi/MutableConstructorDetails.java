/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Mutable form of {@linkplain ConstructorDetails} allowing mutation of the associated annotations.
 *
 * @since 1.3
 * @author Steve Ebersole
 */
public interface MutableConstructorDetails extends ConstructorDetails, MutableAnnotationTarget {
	@Override
	default MutableConstructorDetails asConstructorDetails() {
		return this;
	}
}
