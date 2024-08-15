/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.util.Locale;

import org.hibernate.models.spi.RenderingCollector;

/**
 * @author Steve Ebersole
 */
public class RenderingCollectorImpl implements RenderingCollector {
	private final StringBuilder buffer = new StringBuilder();
	private int currentIndentation = 0;

	@Override
	public void addLine(String line) {
		buffer.append( " " .repeat( currentIndentation ) );
		buffer.append( line );
		buffer.append( "\n" );
	}

	@Override
	public void addLine(String pattern, Object... args) {
		buffer.append( " " .repeat( currentIndentation ) );
		buffer.append( String.format( Locale.ROOT, pattern, args ) );
		buffer.append( "\n" );
	}

	@Override
	public void addLine() {
		buffer.append( "\n" );
	}

	@Override
	public void indent(int depth) {
		currentIndentation += (depth * 4);
	}

	@Override
	public void unindent(int depth) {
		currentIndentation -= (depth * 4);
		assert currentIndentation >= 0;
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
}
