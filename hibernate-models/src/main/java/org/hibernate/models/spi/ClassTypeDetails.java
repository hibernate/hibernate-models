/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

/**
 * Models a class type.
 *
 * @see Class
 * @see org.jboss.jandex.ClassType
 *
 * @author Steve Ebersole
 */
public interface ClassTypeDetails extends ClassBasedTypeDetails {

	@Override
	default ClassTypeDetails asClassType() {
		return this;
	}

}
