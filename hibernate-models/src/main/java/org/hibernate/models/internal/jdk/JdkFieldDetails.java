/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.ModelsException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.SourceModelContext;
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

	public JdkFieldDetails(Field field, JdkClassDetails declaringType, SourceModelBuildingContext buildingContext) {
		super( field::getAnnotations, buildingContext );
		this.field = field;
		this.declaringType = declaringType;
		this.type = new JdkTrackingTypeSwitcher( buildingContext ).switchType( field.getGenericType() );

		this.isArray = field.getType().isArray();
		this.isPlural = isArray
				|| Collection.class.isAssignableFrom( field.getType() )
				|| Map.class.isAssignableFrom( field.getType() );
	}

	@Override
	public String getName() {
		return field.getName();
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
	public Field toJavaMember() {
		return field;
	}

	@Override
	public Field toJavaMember(Class<?> declaringClass, ClassLoading classLoading, SourceModelContext modelContext) {
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

	@Override
	public String toString() {
		return "JdkFieldDetails(" + getName() + ")";
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
