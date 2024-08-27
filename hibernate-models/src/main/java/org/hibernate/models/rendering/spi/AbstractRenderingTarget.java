/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.rendering.spi;

import java.util.Locale;

/**
 * Base support for RenderingTarget implementations.  Handles most details,
 * delegating just the actual appending of characters.
 *
 * @author Steve Ebersole
 */
public abstract class AbstractRenderingTarget implements RenderingTarget {
	public static final int DEFAULT_INDENT_DEPTH = 2;

	private final int indentationDepth;

	private int currentIndentation = 0;

	public AbstractRenderingTarget() {
		this( DEFAULT_INDENT_DEPTH );
	}

	public AbstractRenderingTarget(int indentationDepth) {
		this.indentationDepth = indentationDepth;
	}

	protected abstract void write(String chars);

	@Override
	public void addLine(String line) {
		write( " " .repeat( currentIndentation ) );
		write( line );
		write( "\n" );
	}

	@Override
	public void addLine(String pattern, Object... args) {
		write( " " .repeat( currentIndentation ) );
		write( String.format( Locale.ROOT, pattern, args ) );
		write( "\n" );
	}

	@Override
	public void addLine() {
		write( "\n" );
	}

	@Override
	public void indent(int depth) {
		currentIndentation += (depth * indentationDepth);
	}

	@Override
	public void unindent(int depth) {
		currentIndentation -= (depth * indentationDepth);
		assert currentIndentation >= 0;
	}
}
