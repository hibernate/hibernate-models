/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jandex;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Map;

import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

import org.jboss.jandex.AnnotationTarget;
import org.jboss.jandex.FieldInfo;
import org.jboss.jandex.Type;

import static org.hibernate.models.internal.jandex.JandexTypeSwitchStandard.TYPE_SWITCH_STANDARD;
import static org.hibernate.models.internal.jandex.JandexTypeSwitcher.switchType;

/**
 * @author Steve Ebersole
 */
public class JandexFieldDetails extends AbstractAnnotationTarget implements FieldDetails, MutableMemberDetails {
	private final FieldInfo fieldInfo;
	private final TypeDetails type;
	private final ClassDetails declaringType;

	private final boolean isArray;
	private final boolean isPlural;

	public JandexFieldDetails(
			FieldInfo fieldInfo,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		super( buildingContext );
		this.fieldInfo = fieldInfo;
		this.declaringType = declaringType;
		this.type = switchType( fieldInfo.type(), TYPE_SWITCH_STANDARD, buildingContext );

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

	private Member underlyingMember;

	@Override
	public Member toJavaMember() {
		if ( underlyingMember == null ) {
			underlyingMember = resolveJavaMember();
		}
		return underlyingMember;
	}

	private Field resolveJavaMember() {
		final Class<?> declaringJavaClass = declaringType.toJavaClass();
		try {
			return declaringJavaClass.getField( fieldInfo.name() );
		}
		catch (NoSuchFieldException e) {
			throw new RuntimeException(
					String.format(
							"Jandex FieldInfo had no corresponding Field : %s.%s",
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
}
