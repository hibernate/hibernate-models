/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.RecordComponentInfo;

/**
 * @author Steve Ebersole
 */
public class JandexRecordComponentDetails extends AbstractAnnotationTarget implements RecordComponentDetails, MutableMemberDetails {
	private final RecordComponentInfo recordComponentInfo;
	private final ClassDetails type;

	public JandexRecordComponentDetails(
			RecordComponentInfo recordComponentInfo,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.recordComponentInfo = recordComponentInfo;
		this.type = buildingContext.getClassDetailsRegistry().resolveClassDetails( recordComponentInfo.type().name().toString() );
	}

	@Override
	protected AnnotationTarget getJandexAnnotationTarget() {
		return recordComponentInfo;
	}

	@Override
	public String getName() {
		return recordComponentInfo.name();
	}

	@Override
	public ClassDetails getType() {
		return type;
	}

	@Override
	public int getModifiers() {
		return recordComponentInfo.accessor().flags();
	}

	@Override
	public String toString() {
		return "JandexFieldDetails(" + getName() + ")";
	}
}
