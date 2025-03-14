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
@SubclassableMarker
public class RootClass {
	@MemberMarker
	private Integer value1;
	@Transient
	private Integer value2;
}
