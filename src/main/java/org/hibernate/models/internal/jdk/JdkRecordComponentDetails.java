/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.jdk;

import java.lang.reflect.RecordComponent;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * @author Steve Ebersole
 */
public class JdkRecordComponentDetails extends AbstractAnnotationTarget implements RecordComponentDetails, MutableMemberDetails {
	private final RecordComponent recordComponent;
	private final ClassDetails type;
	private final ClassDetails declaringType;

	public JdkRecordComponentDetails(
			RecordComponent recordComponent,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		super( recordComponent::getAnnotations, buildingContext );
		this.recordComponent = recordComponent;
		this.declaringType = declaringType;
		this.type = buildingContext.getClassDetailsRegistry().resolveClassDetails(
				recordComponent.getType().getName(),
				(n) -> JdkBuilders.buildClassDetailsStatic( recordComponent.getType(), getBuildingContext() )
		);
	}

	@Override
	public String getName() {
		return recordComponent.getName();
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
		return recordComponent.getAccessor().getModifiers();
	}

	@Override
	public String toString() {
		return "JdkRecordComponentDetails(" + getName() + ")";
	}
}
