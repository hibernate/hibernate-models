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
import org.hibernate.models.internal.AnnotationTargetSupport;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.TypeDetails;

import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.field.FieldDescription;

/**
 * @author Steve Ebersole
 */
public class FieldDetailsImpl
		extends AbstractAnnotationTarget
		implements FieldDetails, MutableMemberDetails, AnnotationTargetSupport {
	private final FieldDescription.InDefinedShape underlyingField;
	private final ClassDetailsImpl declaringClassDetails;

	private final TypeDetails type;
	private final boolean isArray;
	private final boolean isPlural;

	public FieldDetailsImpl(
			FieldDescription.InDefinedShape underlyingField,
			ClassDetailsImpl declaringClassDetails,
			SourceModelBuildingContextImpl modelContext) {
		super( modelContext );
		this.underlyingField = underlyingField;
		this.declaringClassDetails = declaringClassDetails;

		this.type = TypeSwitchStandard.switchType( underlyingField.getType(), declaringClassDetails, modelContext );

		this.isArray = underlyingField.getType().isArray();
		this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
	}

	@Override
	protected AnnotationSource getAnnotationSource() {
		return underlyingField;
	}

	@Override
	public String getName() {
		return underlyingField.getName();
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
		return underlyingField.getModifiers();
	}


	private Member underlyingMember;

	@Override
	public Member toJavaMember() {
		if ( underlyingMember == null ) {
			underlyingMember = resolveJavaMember();
		}
		return underlyingMember;
	}

	private Field resolveJavaMember() {
		final Class<?> declaringJavaClass = declaringClassDetails.toJavaClass();
		try {
			return declaringJavaClass.getField( getName() );
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(
					String.format(
							"Jandex FieldInfo had no corresponding Field : %s.%s",
							declaringJavaClass.getName(),
							getName()
					),
					e
			);
		}
	}

	@Override
	public String toString() {
		return "JandexFieldDetails(" + getName() + ")";
	}

	@Override
	public FieldDetails asFieldDetails() {
		return this;
	}

	@Override
	public MutableMemberDetails asMemberDetails() {
		return this;
	}

	@Override
	public MethodDetails asMethodDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast as MethodDetails" );
	}

	@Override
	public RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast as RecordComponentDetails" );
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
