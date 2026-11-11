/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import jakarta.annotation.Nonnull;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.ModelsException;
import org.hibernate.models.jdk.internal.AbstractJdkAnnotationTarget;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * @author Steve Ebersole
 */
public class JdkFieldDetails extends AbstractJdkAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final Field field;
	private final JdkClassDetails declaringType;
	private final TypeDetails type;

	private final boolean isArray;
	private final boolean isPlural;

	public JdkFieldDetails(@Nonnull Field field, @Nonnull JdkClassDetails declaringType, @Nonnull ModelsContext modelsContext) {
		super( field::getAnnotations, modelsContext );
		this.field = field;
		this.declaringType = declaringType;
		this.type = new JdkTrackingTypeSwitcher( modelsContext ).switchType( field.getGenericType() );

		this.isArray = field.getType().isArray();
		this.isPlural = isArray
				|| Collection.class.isAssignableFrom( field.getType() )
				|| Map.class.isAssignableFrom( field.getType() );
	}

	@Nonnull
	@Override
	public String getName() {
		return field.getName();
	}

	@Nonnull
	@Override
	public TypeDetails getType() {
		return type;
	}

	@Nonnull
	@Override
	public ClassDetails getDeclaringType() {
		return declaringType;
	}

	@Nonnull
	@Override
	public Field toJavaMember() {
		return field;
	}

	@Nonnull
	@Override
	public Field toJavaMember(@Nonnull Class<?> declaringClass, @Nonnull ClassLoading classLoading, @Nonnull ModelsContext modelContext) {
		if ( declaringClass == field.getDeclaringClass() ) {
			return field;
		}
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
	public boolean isPlural() {
		return isPlural;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public int getModifiers() {
		return field.getModifiers();
	}

	@Nonnull
	@Override
	public String toString() {
		return "JdkFieldDetails(" + getName() + ")";
	}

	@Nonnull
	@Override
	public FieldDetails asFieldDetails() {
		return this;
	}

	@Nonnull
	@Override
	public MutableMemberDetails asMemberDetails() {
		return this;
	}

	@Nonnull
	@Override
	public MethodDetails asMethodDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast as MethodDetails" );
	}

	@Nonnull
	@Override
	public RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast as RecordComponentDetails" );
	}

	@Nonnull
	@Override
	public MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast as ClassDetails" );
	}

	@Nonnull
	@Override
	public <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "FieldDetails cannot be cast as AnnotationDescriptor" );
	}
}
