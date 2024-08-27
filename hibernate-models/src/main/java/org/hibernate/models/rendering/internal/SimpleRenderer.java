/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.rendering.internal;

import org.hibernate.models.rendering.spi.AbstractRenderer;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.rendering.spi.RenderingTarget;
import org.hibernate.models.spi.SourceModelContext;

/**
 * A Renderer with a simplified output format.
 *
 * @author Steve Ebersole
 */
public class SimpleRenderer extends AbstractRenderer {
	private final RenderingTarget renderingTarget;

	public SimpleRenderer(RenderingTarget renderingTarget) {
		this.renderingTarget = renderingTarget;
	}

	@Override
	protected RenderingTarget getRenderingTarget() {
		return renderingTarget;
	}

	@Override
	public void renderClassDetails(ClassDetails classDetails, SourceModelContext context) {
		final String typeDeclarationPattern;
		if ( classDetails.isInterface() ) {
			typeDeclarationPattern = "interface %s {";
		}
		else if ( classDetails.isRecord() ) {
			typeDeclarationPattern = "record %s {";
		}
		else {
			typeDeclarationPattern = "class %s {";
		}

		renderingTarget.addLine( typeDeclarationPattern, classDetails.getName() );
		renderingTarget.indent( 1 );

		renderingTarget.addLine( "// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		renderingTarget.addLine( "// fields" );
		classDetails.forEachField( (index, fieldDetails) -> {
			renderField( fieldDetails, context );
			renderingTarget.addLine();
		} );
		renderingTarget.addLine();

		renderingTarget.addLine( "// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
		renderingTarget.addLine( "// methods" );
		classDetails.forEachMethod( (index, methodDetails) -> {
			renderMethod( methodDetails, context );
			renderingTarget.addLine();
		} );
		renderingTarget.addLine();

		if ( classDetails.isRecord() ) {
			renderingTarget.addLine( "// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~" );
			renderingTarget.addLine( "// record components" );
			classDetails.forEachRecordComponent( (index, recordComponentDetails) -> {
				renderRecordComponent( recordComponentDetails, context );
				renderingTarget.addLine();
			} );
		}

		renderingTarget.unindent( 1 );
		renderingTarget.addLine( "}" );
	}

	@Override
	public void renderFieldDetails(FieldDetails fieldDetails, SourceModelContext context) {
		// todo : would be nice to render the type-details to include generics, etc
		renderingTarget.addLine( "%s %s", fieldDetails.getType().determineRawClass().getName(), fieldDetails.getName() );
	}

	@Override
	public void renderMethodDetails(MethodDetails methodDetails, SourceModelContext context) {
		// todo : would be nice to render the type-details to include generics, etc
		renderingTarget.addLine(
				"%s %s (%s)",
				methodDetails.getType() == null
						? "void"
						: methodDetails.getType().determineRawClass().getName(),
				methodDetails.getName(),
				methodDetails.getMethodKind().name()
		);

		renderingTarget.indent( 2 );
		methodDetails.getArgumentTypes().forEach( (arg) -> renderingTarget.addLine( " - %s", arg.getName() ) );
		renderingTarget.unindent( 2 );
	}

	@Override
	public void renderRecordComponentDetails(RecordComponentDetails recordComponentDetails, SourceModelContext context) {
		// todo : would be nice to render the type-details to include generics, etc
		renderingTarget.addLine( "%s %s", recordComponentDetails.getType().determineRawClass().getName(), recordComponentDetails.getName() );
	}
}
