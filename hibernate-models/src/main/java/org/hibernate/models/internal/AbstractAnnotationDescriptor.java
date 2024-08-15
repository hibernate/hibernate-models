/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.EnumSet;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

/**
 * Base support for {@link AnnotationDescriptor} implementations
 *
 * @author Steve Ebersole
 */
public abstract class AbstractAnnotationDescriptor<A extends Annotation>
		extends AbstractAnnotationTarget
		implements AnnotationDescriptor<A> {
	private final Class<A> annotationType;
	private final EnumSet<Kind> allowableTargets;

	private final boolean inherited;
	private final AnnotationDescriptor<?> repeatableContainer;

	public AbstractAnnotationDescriptor(
			Class<A> annotationType,
			EnumSet<Kind> allowableTargets,
			boolean inherited,
			AnnotationDescriptor<?> repeatableContainer) {
		this.annotationType = annotationType;
		this.allowableTargets = allowableTargets;
		this.inherited = inherited;
		this.repeatableContainer = repeatableContainer;
	}

	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	@Override
	public String getName() {
		return annotationType.getName();
	}

	@Override
	public EnumSet<Kind> getAllowableTargets() {
		return allowableTargets;
	}

	@Override
	public boolean isInherited() {
		return inherited;
	}

	@Override
	public AnnotationDescriptor<?> getRepeatableContainer() {
		return repeatableContainer;
	}

	@Override
	public Collection<? extends Annotation> getDirectAnnotationUsages() {
		return getUsageMap().values();
	}

	@Override
	public <X extends Annotation> X[] getRepeatedAnnotationUsages(
			AnnotationDescriptor<X> type,
			SourceModelBuildingContext modelContext) {
		return AnnotationUsageHelper.getRepeatedUsages( type, getUsageMap(), modelContext );
	}

	@Override
	public <X extends Annotation> X[] getRepeatedAnnotationUsages(Class<X> type, SourceModelBuildingContext modelContext) {
		return getRepeatedAnnotationUsages( modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ), modelContext );
	}

	@Override
	public <X extends Annotation> AnnotationDescriptor<X> asAnnotationDescriptor() {
		//noinspection unchecked
		return (AnnotationDescriptor<X>) this;
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to ClassDetails" );
	}

	@Override
	public MutableMemberDetails asMemberDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to MemberDetails" );
	}

	@Override
	public FieldDetails asFieldDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to FieldDetails" );
	}

	@Override
	public MethodDetails asMethodDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to MethodDetails" );
	}

	@Override
	public RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to RecordComponentDetails" );
	}
}
