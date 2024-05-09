/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.AnnotationHelper;
import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.internal.RenderingCollectorImpl;

/**
 * Models a {@linkplain java.lang.reflect.Field field} in a {@linkplain ClassDetails class}
 *
 * @author Steve Ebersole
 */
public interface FieldDetails extends MemberDetails {
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
	default FieldDetails asFieldDetails() {
		return this;
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast to MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "FieldDetails cannot be cast to RecordComponentDetails" );
	}

	@Override
	default void render(SourceModelBuildingContext modelContext) {
		final RenderingCollectorImpl renderingCollector = new RenderingCollectorImpl();
		render( renderingCollector,modelContext );
		renderingCollector.render();
	}

	@Override
	default void render(RenderingCollector collector, SourceModelBuildingContext modelContext) {
		forEachDirectAnnotationUsage( (usage) -> AnnotationHelper.render( collector, usage, modelContext ) );
		// todo : would be nice to render the type-details to include generics, etc
		collector.addLine( "%s %s", getType().determineRawClass().getName(), getName() );
	}
}
