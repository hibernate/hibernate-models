/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;


/**
 * @author Steve Ebersole
 */
public class JdkFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
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
}
