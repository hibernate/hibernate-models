/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.rendering.internal;

import org.hibernate.models.rendering.SimpleRenderer;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RecordComponentDetails;

/**
 * Internal support for the rendering methods exposed by the model contracts.
 *
 * @since 2.0
 * @author Steve Ebersole
 */
public final class RenderingHelper {
	private RenderingHelper() {
	}

	public static String renderClass(
			ClassDetails classDetails,
			ModelsContext modelsContext,
			ClassDetails.RenderMode mode) {
		final RenderingTargetCollectingImpl target = new RenderingTargetCollectingImpl();
		final SimpleRenderer renderer = mode == ClassDetails.RenderMode.COMPLETE
				? new SimpleRenderer( target )
				: new SimpleRenderer( target ) {
					@Override
					protected boolean renderMembers() {
						return false;
					}
				};
		renderer.renderClass( classDetails, modelsContext );
		return target.toString();
	}

	public static String renderField(FieldDetails fieldDetails, ModelsContext modelsContext) {
		final RenderingTargetCollectingImpl target = new RenderingTargetCollectingImpl();
		new SimpleRenderer( target ).renderField( fieldDetails, modelsContext );
		return target.toString();
	}

	public static String renderMethod(MethodDetails methodDetails, ModelsContext modelsContext) {
		final RenderingTargetCollectingImpl target = new RenderingTargetCollectingImpl();
		new SimpleRenderer( target ).renderMethod( methodDetails, modelsContext );
		return target.toString();
	}

	public static String renderRecordComponent(
			RecordComponentDetails recordComponentDetails,
			ModelsContext modelsContext) {
		final RenderingTargetCollectingImpl target = new RenderingTargetCollectingImpl();
		new SimpleRenderer( target ).renderRecordComponent( recordComponentDetails, modelsContext );
		return target.toString();
	}
}
