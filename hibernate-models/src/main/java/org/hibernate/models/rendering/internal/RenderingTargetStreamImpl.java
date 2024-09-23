/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.rendering.internal;

import java.io.IOException;

import org.hibernate.models.rendering.RenderingException;
import org.hibernate.models.rendering.spi.AbstractRenderingTarget;

/**
 * RenderingTarget implementation which collects the rendering into an internal buffer.
 *
 * @author Steve Ebersole
 */
public class RenderingTargetStreamImpl extends AbstractRenderingTarget {
	private final Appendable outputStream;

	public RenderingTargetStreamImpl() {
		this( System.out );
	}

	public RenderingTargetStreamImpl(int indentationDepth) {
		this( System.out, indentationDepth );
	}

	public RenderingTargetStreamImpl(Appendable outputStream) {
		this( outputStream, DEFAULT_INDENT_DEPTH );
	}

	public RenderingTargetStreamImpl(Appendable outputStream, int indentationDepth) {
		super( indentationDepth );
		this.outputStream = outputStream;
	}

	@Override
	protected void write(String chars) {
		try {
			outputStream.append( chars );
		}
		catch (IOException e) {
			throw new RenderingException( "Error adding characters to stream (Appendable): " + chars, e );
		}
	}
}
