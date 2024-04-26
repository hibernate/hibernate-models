/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.beans.Introspector;
import java.util.List;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.RenderingCollectorImpl;

import static org.hibernate.models.internal.ModifierUtils.hasPersistableMethodModifiers;

/**
 * Models a {@linkplain java.lang.reflect.Method method} in a {@linkplain ClassDetails class}.
 *
 * @author Steve Ebersole
 */
public interface MethodDetails extends MemberDetails {
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
			return Introspector.decapitalize( methodName.substring( 2 ) );
		}
		else if ( methodName.startsWith( "get" ) ) {
			return Introspector.decapitalize( methodName.substring( 3 ) );
		}

		return null;
	}

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast to FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		return this;
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "MethodDetails cannot be cast to RecordComponentDetails" );
	}

	@Override
	default void render() {
		final RenderingCollectorImpl renderingCollector = new RenderingCollectorImpl();
		render( renderingCollector );
		renderingCollector.render();
	}

	@Override
	default void render(RenderingCollector collector) {
		forAllAnnotationUsages( (usage) -> usage.render( collector ) );

		// todo : would be nice to render the type-details to include generics, etc
		collector.addLine(
				"%s %s (%s)",
				getType() == null
						? "void"
						: getType().determineRawClass().getName(),
				getName(),
				getMethodKind().name()
		);

		collector.indent( 2 );
		getArgumentTypes().forEach( (arg) -> collector.addLine( " - %s", arg.getName() ) );
		collector.unindent( 2 );
	}
}
