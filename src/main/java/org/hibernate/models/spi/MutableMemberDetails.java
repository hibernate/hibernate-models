/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import org.hibernate.models.IllegalCastException;

/**
 * Union of MemberDetails and MutableAnnotationTarget
 *
 * @author Steve Ebersole
 */
public interface MutableMemberDetails extends MemberDetails, MutableAnnotationTarget {
	@Override
	default MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "MemberDetails cannot be cast to ClassDetails" );
	}

	@Override
	default MutableMemberDetails asMemberDetails() {
		return this;
	}
}
