/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.RecordComponent;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * @author Steve Ebersole
 */
public class JdkRecordComponentDetails extends AbstractJdkAnnotationTarget
		implements RecordComponentDetails, MutableMemberDetails {
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
	public Member toJavaMember(Class<?> declaringClass, ClassLoading classLoading, SourceModelContext modelContext) {
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
		throw new IllegalCastException( "RecordComponentDetails cannot be cast as MethodDetails" );
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast as ClassDetails" );
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast as AnnotationDescriptor" );
	}
}
