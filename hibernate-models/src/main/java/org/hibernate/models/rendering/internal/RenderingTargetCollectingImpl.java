/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.rendering.internal;

import java.io.PrintStream;

import org.hibernate.models.rendering.spi.AbstractRenderingTarget;

/**
 * RenderingTarget implementation which collects the rendering into an internal buffer.
 *
 * @author Steve Ebersole
 */
public class RenderingTargetCollectingImpl extends AbstractRenderingTarget {
	private final StringBuilder buffer = new StringBuilder();

	public RenderingTargetCollectingImpl() {
		super();
	}

	public RenderingTargetCollectingImpl(int indentationDepth) {
		super( indentationDepth );
	}

	@Override
	protected void write(String chars) {
		buffer.append( chars );
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	public void render() {
		render( System.out );
	}

	public void render(PrintStream printStream) {
		printStream.println( this );
	}
}
