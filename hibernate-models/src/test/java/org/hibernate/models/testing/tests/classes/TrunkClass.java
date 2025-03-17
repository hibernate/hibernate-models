/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.classes;

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@ClassMarker
public class TrunkClass extends RootClass {
	@MemberMarker
	private Integer value3;
	@Transient
	private Integer value4;
}
