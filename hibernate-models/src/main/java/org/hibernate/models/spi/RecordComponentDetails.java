/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.spi;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.AnnotationHelper;
import org.hibernate.models.internal.RenderingCollectorImpl;

import static org.hibernate.models.spi.AnnotationTarget.Kind.RECORD_COMPONENT;

/**
 * Models a {@linkplain java.lang.reflect.RecordComponent component} in a {@linkplain ClassDetails record}
 *
 * @author Steve Ebersole
 */
public interface RecordComponentDetails extends MemberDetails {
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
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast to FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "RecordComponentDetails cannot be cast to MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		return this;
	}

	@Override
	default void render(SourceModelBuildingContext modelContext) {
		final RenderingCollectorImpl renderingCollector = new RenderingCollectorImpl();
		render( renderingCollector, modelContext );
		renderingCollector.render();
	}

	@Override
	default void render(RenderingCollector collector, SourceModelBuildingContext modelContext) {
		forEachDirectAnnotationUsage( (usage) -> AnnotationHelper.render( collector, usage, modelContext ) );
		// todo : would be nice to render the type-details to include generics, etc
		collector.addLine( "%s %s", getType().determineRawClass().getName(), getName() );
	}

}
