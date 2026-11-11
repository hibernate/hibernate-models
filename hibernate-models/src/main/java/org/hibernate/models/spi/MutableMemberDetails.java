/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;

/**
 * Union of MemberDetails and MutableAnnotationTarget
 *
 * @author Steve Ebersole
 */
public interface MutableMemberDetails extends MemberDetails, MutableAnnotationTarget {
	@Nonnull
	@Override
	default MutableMemberDetails asMemberDetails() {
		return this;
	}
}
