/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.classes;

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@ClassMarker
public class LeafClass extends BranchClass {
	@MemberMarker
	private Integer value7;
	@Transient
	private Integer value8;
}
