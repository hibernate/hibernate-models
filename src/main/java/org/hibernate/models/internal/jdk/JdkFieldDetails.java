/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.reflect.Field;
import java.lang.reflect.Member;

import org.hibernate.models.internal.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;


/**
 * @author Steve Ebersole
 */
public class JdkFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final Field field;
	private final JdkClassDetails declaringType;
	private final ClassDetails type;

	public JdkFieldDetails(Field field, JdkClassDetails declaringType, SourceModelBuildingContext buildingContext) {
		super( field::getAnnotations, buildingContext );
		this.field = field;
		this.declaringType = declaringType;
		this.type = buildingContext.getClassDetailsRegistry().resolveClassDetails(
				field.getType().getName(),
				(n) -> JdkBuilders.buildClassDetailsStatic( field.getType(), getBuildingContext() )
		);
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public ClassDetails getType() {
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
	public int getModifiers() {
		return field.getModifiers();
	}

	@Override
	public String toString() {
		return "JdkFieldDetails(" + getName() + ")";
	}
}
