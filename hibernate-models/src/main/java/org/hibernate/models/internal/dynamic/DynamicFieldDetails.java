/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.dynamic;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Locale;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.ModelsException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MemberDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableScope;

/**
 * FieldDetails which does not necessarily map to a physical Field (dynamic models).
 *
 * @see org.hibernate.models.internal.ModifierUtils#DYNAMIC_ATTRIBUTE_MODIFIERS
 *
 * @author Steve Ebersole
 */
public class DynamicFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final String name;
	private final TypeDetails type;
	private final ClassDetails declaringType;
	private final int modifierFlags;

	private final boolean isArray;
	private final boolean isPlural;

	/**
	 * Constructs a dynamic FieldDetails.
	 *
	 * @param name The name of the "field"
	 * @param type The type of the "field"
	 * @param declaringType The type on which the "field" is declared
	 * @param modifierFlags The modifier flags (public, static, etc.) for the "field".  Typically, callers should use {@linkplain org.hibernate.models.internal.ModifierUtils#DYNAMIC_ATTRIBUTE_MODIFIERS} when
	 * 		defining a field to be used as a {@linkplain MemberDetails#isPersistable() persistable} member.
	 * @param isArray Whether the "field" is an array
	 * @param isPlural Whether the "field" is plural
	 * @param buildingContext Context for the creation (access to useful information).
	 */
	public DynamicFieldDetails(
			String name,
			TypeDetails type,
			ClassDetails declaringType,
			int modifierFlags,
			boolean isArray,
			boolean isPlural,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.name = name;
		this.type = type;
		this.declaringType = declaringType;
		this.modifierFlags = modifierFlags;
		this.isArray = isArray;
		this.isPlural = isPlural;

		assert isPersistable();
	}

	@Override
	public String getName() {
		return name;
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
		return modifierFlags;
	}

	private Field field;

	@Override
	public Field toJavaMember() {
		if ( field == null && getDeclaringType().getClassName() != null ) {
			final Class<?> declaringClass = getDeclaringType().toJavaClass();
			field = toJavaMember( declaringClass, getModelContext().getClassLoading(), getModelContext() );
		}
		return field;
	}

	@Override
	public Field toJavaMember(Class<?> declaringClass, ClassLoading classLoading, SourceModelContext modelContext) {
		try {
			// make sure the type ends up on the given class-loading
			type.determineRawClass().toJavaClass( classLoading, modelContext );
			return declaringClass.getDeclaredField( getName() );
		}
		catch (NoSuchFieldException e) {
			throw new ModelsException(
					String.format(
							Locale.ROOT,
							"Unable to locate field `%s` on %s",
							getName(),
							declaringClass.getName()
					),
					e
			);
		}
	}

	@Override
	public TypeDetails resolveRelativeType(TypeVariableScope container) {
		return type;
	}

	@Override
	public String toString() {
		return "DynamicFieldDetails(" + name + ")";
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
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast as ClassDetails" );
	}

	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "FieldDetails cannot be cast as AnnotationDescriptor" );
	}
}
