/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.FieldInfo;

/**
 * @author Steve Ebersole
 */
public class JandexFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final FieldInfo fieldInfo;
	private final ClassDetails type;
	private final ClassDetails declaringType;

	public JandexFieldDetails(
			FieldInfo fieldInfo,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.fieldInfo = fieldInfo;
		this.declaringType = declaringType;
		this.type = buildingContext.getClassDetailsRegistry().resolveClassDetails( fieldInfo.type().name().toString() );
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return fieldInfo;
	}

	@Override
	public String getName() {
		return fieldInfo.name();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public ClassDetails getDeclaringType() {
		return declaringType;
	}

	@Override
	public int getModifiers() {
		return fieldInfo.flags();
	}

	@Override
	public String toString() {
		return "JandexFieldDetails(" + getName() + ")";
	}
}
