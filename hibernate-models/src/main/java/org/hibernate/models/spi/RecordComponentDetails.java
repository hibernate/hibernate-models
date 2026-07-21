/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Member;

import org.hibernate.models.rendering.internal.RenderingHelper;

import static org.hibernate.models.spi.AnnotationTarget.Kind.RECORD_COMPONENT;

/**
 * Models a {@linkplain java.lang.reflect.RecordComponent component} in a {@linkplain ClassDetails record}
 *
 * @author Steve Ebersole
 */
public interface RecordComponentDetails extends MemberDetails {
	/**
	 * Render this record component and its directly associated annotations.
	 * Contained annotation values are rendered recursively.
	 */
	default String render(ModelsContext modelsContext) {
		return RenderingHelper.renderRecordComponent( this, modelsContext );
	}

	@Override
	default Kind getKind() {
		return RECORD_COMPONENT;
	}

	@Override
	default String resolveAttributeName() {
		return getName();
	}

	@Override
	default boolean isPersistable() {
		return true;
	}

	@Override
	Member toJavaMember();

	@Override
	Member toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext);

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		return this;
	}

}
