/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.RecordComponentInfo;
import org.jboss.jandex.Type;

import static org.hibernate.models.internal.jandex.JandexTypeSwitchStandard.switchType;

/**
 * @author Steve Ebersole
 */
public class JandexRecordComponentDetails extends AbstractAnnotationTarget implements RecordComponentDetails, MutableMemberDetails {
	private final RecordComponentInfo recordComponentInfo;
	private final TypeDetails type;
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
		this.type = switchType( recordComponentInfo.type(), declaringType, buildingContext );

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

	@Override
	public RecordComponentDetails asRecordComponentDetails() {
		return this;
	}

	@Override
	public MutableMemberDetails asMemberDetails() {
		return this;
	}

	@Override
	public FieldDetails asFieldDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast as FieldDetails" );
	}

	@Override
	public MethodDetails asMethodDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast as FieldDetails" );
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast to an AnnotationDescriptor" );
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast to a ClassDetails" );
	}
}
