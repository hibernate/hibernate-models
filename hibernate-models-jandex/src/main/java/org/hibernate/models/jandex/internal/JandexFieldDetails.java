/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.ModelsException;
import org.hibernate.models.internal.AnnotationTargetSupport;
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

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Type;

/**
 * @author Steve Ebersole
 */
public class JandexFieldDetails extends AbstractAnnotationTarget
		implements FieldDetails, MutableMemberDetails, AnnotationTargetSupport {
	private final FieldInfo fieldInfo;
	private final TypeDetails type;
	private final ClassDetails declaringType;

	private final boolean isArray;
	private final boolean isPlural;

	public JandexFieldDetails(
			FieldInfo fieldInfo,
			ClassDetails declaringType,
			ModelsContext modelsContext) {
		super( modelsContext );
		this.fieldInfo = fieldInfo;
		this.declaringType = declaringType;
		this.type = JandexTypeSwitchStandard.switchType( fieldInfo.type(), declaringType, modelsContext );

		this.isArray = fieldInfo.type().kind() == Type.Kind.ARRAY;
		this.isPlural = isArray || type.isImplementor( Collection.class ) || type.isImplementor( Map.class );
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
		return fieldInfo.flags();
	}

	private Field underlyingMember;

	@Override
	public Field toJavaMember() {
		if ( underlyingMember == null ) {
			final Class<?> declaringJavaClass = declaringType.toJavaClass();
			underlyingMember = resolveJavaMember( declaringJavaClass);
		}
		return underlyingMember;
	}

	@Override
	public Field toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext) {
		// make sure the type ends up on the given class-loading
		type.determineRawClass().toJavaClass( classLoading, modelContext );
		return resolveJavaMember( declaringClass );
	}

	private Field resolveJavaMember(Class<?> declaringJavaClass) {
		try {
			return declaringJavaClass.getField( fieldInfo.name() );
		}
		catch (NoSuchFieldException e) {
			throw new ModelsException(
					String.format(
							"Unable to locate field `%s` on %s",
							declaringType.getName(),
							fieldInfo.name()
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
