/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal.jdk;

import java.lang.reflect.Member;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * @author Steve Ebersole
 */
public class JdkRecordComponentDetails extends AbstractAnnotationTarget implements RecordComponentDetails, MutableMemberDetails {
	private final RecordComponent recordComponent;
	private final TypeDetails type;
	private final ClassDetails declaringType;

	private final boolean isArray;
	private final boolean isPlural;

	public JdkRecordComponentDetails(
			RecordComponent recordComponent,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		super( recordComponent::getAnnotations, buildingContext );
		this.recordComponent = recordComponent;
		this.declaringType = declaringType;
		this.type = JdkTrackingTypeSwitcher.standardSwitchType( recordComponent.getGenericType(), buildingContext );

		this.isArray = recordComponent.getType().isArray();
		this.isPlural = isArray
				|| Collection.class.isAssignableFrom( recordComponent.getType() )
				|| Map.class.isAssignableFrom( recordComponent.getType() );
	}

	@Override
	public String getName() {
		return recordComponent.getName();
	}

	@Override
	public TypeDetails getType() {
		return type;
	}

	@Override
	public ClassDetails getDeclaringType() {
		return declaringType;
	}

	@Override
	public boolean isPlural() {
		return isPlural;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public Member toJavaMember() {
		// we could maybe resolve the corresponding method...
		return null;
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
