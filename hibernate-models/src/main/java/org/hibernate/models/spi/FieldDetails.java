/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Field;

import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.rendering.internal.RenderingHelper;

/**
 * Models a {@linkplain java.lang.reflect.Field field} in a {@linkplain ClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface FieldDetails extends MemberDetails {
	/**
	 * Render this field and its directly associated annotations.  Contained
	 * annotation values are rendered recursively.
	 */
	default String render(ModelsContext modelsContext) {
		return RenderingHelper.renderField( this, modelsContext );
	}

	@Override
	default Kind getKind() {
		return Kind.FIELD;
	}

	@Override
	default String resolveAttributeName() {
		return getName();
	}

	@Override
	default boolean isPersistable() {
		return ModifierUtils.hasPersistableFieldModifiers( getModifiers() );
	}

	@Override
	Field toJavaMember();

	@Override
	Field toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext);

	@Override
	default FieldDetails asFieldDetails() {
		return this;
	}
}
