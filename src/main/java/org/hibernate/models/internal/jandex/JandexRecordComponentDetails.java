/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.Type;

/**
 * @author Steve Ebersole
 */
public class JandexRecordComponentDetails extends AbstractAnnotationTarget implements RecordComponentDetails, MutableMemberDetails {
	private final RecordComponentInfo recordComponentInfo;
	private final ClassDetails type;
	private final ClassDetails declaringType;

	private final boolean isArray;
	private final boolean isPlural;

	public JandexRecordComponentDetails(
			RecordComponentInfo recordComponentInfo,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.recordComponentInfo = recordComponentInfo;
		this.declaringType = declaringType;
		this.type = buildingContext.getClassDetailsRegistry().resolveClassDetails( recordComponentInfo.type().name().toString() );

		this.isArray = recordComponentInfo.type().kind() == Type.Kind.ARRAY;
		this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
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
	public int getModifiers() {
		return recordComponentInfo.accessor().flags();
	}

	@Override
	public Member toJavaMember() {
		// we could maybe resolve the corresponding method...
		return null;
	}

	@Override
	public String toString() {
		return "JandexFieldDetails(" + getName() + ")";
	}
}
