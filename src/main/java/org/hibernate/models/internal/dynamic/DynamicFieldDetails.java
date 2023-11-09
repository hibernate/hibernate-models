/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class DynamicFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final String name;
	private final ClassDetails type;
	private final boolean isPersistable;

	public DynamicFieldDetails(String name, ClassDetails type, SourceModelBuildingContext buildingContext) {
		this( name, type, true, buildingContext );
	}

	public DynamicFieldDetails(
			String name,
			ClassDetails type,
			boolean isPersistable,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.isPersistable = isPersistable;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public boolean isPersistable() {
		return isPersistable;
	}
}
