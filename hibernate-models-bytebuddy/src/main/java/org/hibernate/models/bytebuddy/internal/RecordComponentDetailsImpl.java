/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.type.RecordComponentDescription;

/**
 * @author Steve Ebersole
 */
public class RecordComponentDetailsImpl
		extends AbstractAnnotationTarget
		implements RecordComponentDetails, MutableMemberDetails {
	private final RecordComponentDescription.InDefinedShape underlyingComponent;
	private final ClassDetails declaringClassDetails;
	private final TypeDetails type;

	private final boolean isArray;
	private final boolean isPlural;

	public RecordComponentDetailsImpl(
			RecordComponentDescription.InDefinedShape underlyingComponent,
			ClassDetailsImpl declaringClassDetails,
			ByteBuddyModelsContext modelContext) {
		super( modelContext );
		this.underlyingComponent = underlyingComponent;
		this.declaringClassDetails = declaringClassDetails;

		this.type = TypeSwitchStandard.switchType( underlyingComponent.getType(), declaringClassDetails, modelContext );

		this.isArray = underlyingComponent.getType().isArray();
		this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
	}

	@Override
	protected AnnotationSource getAnnotationSource() {
		return underlyingComponent;
	}

	@Override
	public String getName() {
		return underlyingComponent.getActualName();
	}

	@Override
	public TypeDetails getType() {
		return type;
	}

	@Override
	public ClassDetails getDeclaringType() {
		return declaringClassDetails;
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
		return underlyingComponent.getAccessor().getModifiers();
	}


	private Member underlyingMember;

	@Override
	public Member toJavaMember() {
		if ( underlyingMember == null ) {
			underlyingMember = resolveJavaMember();
		}
		return underlyingMember;
	}

	@Override
	public Member toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext) {
		// we could maybe resolve the corresponding method...
		return null;
	}

	private Field resolveJavaMember() {
		final Class<?> declaringJavaClass = declaringClassDetails.toJavaClass();
		try {
			return declaringJavaClass.getField( getName() );
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(
					String.format(
							"Has no corresponding record-component : %s.%s",
							declaringJavaClass.getName(),
							getName()
					),
					e
			);
		}
	}

	@Override
	public String toString() {
		return "RecordComponentDetails(" + getName() + ")";
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
		throw new IllegalCastException( "FieldDetails cannot be cast as MethodDetails" );
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "FieldDetails cannot be cast to an AnnotationDescriptor" );
	}

	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast to a ClassDetails" );
	}
}
