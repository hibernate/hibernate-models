/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.rendering.internal.RenderingHelper;

import static org.hibernate.models.internal.ModifierUtils.hasPersistableMethodModifiers;

/**
 * Models a {@linkplain java.lang.reflect.Method method} in a {@linkplain ClassDetails class}.
 *
 * @author Steve Ebersole
 */
public interface MethodDetails extends MemberDetails {
	/**
	 * Render this method and its directly associated annotations.  Contained
	 * annotation values are rendered recursively.
	 */
	default String render(ModelsContext modelsContext) {
		return RenderingHelper.renderMethod( this, modelsContext );
	}

	enum MethodKind {
		GETTER,
		SETTER,
		OTHER
	}

	MethodKind getMethodKind();

	@Override
	default Kind getKind() {
		return Kind.METHOD;
	}

	ClassDetails getReturnType();

	List<ClassDetails> getArgumentTypes();

	@Override
	default boolean isPersistable() {
		return getMethodKind() == MethodKind.GETTER
				&& hasPersistableMethodModifiers( getModifiers() );
	}

	@Override
	default String resolveAttributeName() {
		final String methodName = getName();

		if ( methodName.startsWith( "is" ) ) {
			return StringHelper.decapitalize( methodName.substring( 2 ) );
		}
		else if ( methodName.startsWith( "get" ) ) {
			return StringHelper.decapitalize( methodName.substring( 3 ) );
		}

		return null;
	}

	@Override
	Method toJavaMember();

	@Override
	Method toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext);

	@Override
	default MethodDetails asMethodDetails() {
		return this;
	}

	/**
	 * The {@link RecordComponentDetails} that structurally corresponds to this method,
	 * or {@code null} if this method is not a record component accessor (i.e. the
	 * declaring type is not a record, or the method takes arguments).
	 *
	 * @return the corresponding record component, or {@code null}
	 */
	default RecordComponentDetails getCorrespondingRecordComponent() {
		if ( !getDeclaringType().isRecord() ) {
			return null;
		}
		// record accessors don't follow the JavaBean get/is naming convention, so JdkBuilders classifies them as MethodKind.OTHER
		if ( !getArgumentTypes().isEmpty() ) {
			return null;
		}
		return getDeclaringType().findRecordComponentByName( getName() );
	}

}
