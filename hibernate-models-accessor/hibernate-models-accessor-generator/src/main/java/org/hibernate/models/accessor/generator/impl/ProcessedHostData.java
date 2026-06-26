/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.impl;

import java.util.Set;
import java.util.TreeSet;

import org.hibernate.models.accessor.generator.AccessorClassMetadata;

public record ProcessedHostData(
		AccessorClassMetadata.TypeMetadata type,
		Set<AccessorClassMetadata.MemberMetadata> readers,
		Set<AccessorClassMetadata.MemberMetadata> writers,
		Set<AccessorClassMetadata.ConstructorMetadata> constructors) {

	public ProcessedHostData(AccessorClassMetadata.TypeMetadata type) {
		this( type, new TreeSet<>(), new TreeSet<>(), new TreeSet<>() );
	}
}
