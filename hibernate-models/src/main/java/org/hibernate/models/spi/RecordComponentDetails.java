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

	/**
	 * The {@link FieldDetails} representing the private synthetic backing field
	 * for this record component.
	 *
	 * @return the corresponding backing field; never {@code null} for a well-formed record
	 *
	 * @apiNote Implementors may override this default to use a more direct lookup
	 *          (e.g. via {@code RecordComponent} or {@code RecordComponentInfo}) rather
	 *          than a generic name-based search.
	 */
	default FieldDetails getField() {
		return getDeclaringType().findFieldByName( getName() );
	}

	/**
	 * The {@link MethodDetails} representing the accessor method for this record component.
	 * <p>
	 * The accessor shares its name with the component and takes no parameters.
	 * Note that record accessors do not follow JavaBean {@code get}/{@code is} naming
	 * conventions and are therefore classified as {@link MethodDetails.MethodKind#OTHER} rather than
	 * {@link MethodDetails.MethodKind#GETTER}.
	 *
	 * @return the corresponding accessor method; never {@code null} for a well-formed record
	 *
	 * @apiNote Implementors may override this default to use a more direct lookup
	 *          (e.g. via {@link java.lang.reflect.RecordComponent#getAccessor()}) rather
	 *          than a linear scan of the declaring type's methods.
	 */
	default MethodDetails getAccessor() {
		for ( MethodDetails method : getDeclaringType().getMethods() ) {
			if ( method.getName().equals( getName() ) && method.getArgumentTypes().isEmpty() ) {
				return method;
			}
		}
		return null;
	}

}
