/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.hibernate.models.serial.spi.RestoredModels;

/// Object stream used by an owning deserialization process to resolve model
/// archive references back to restored Hibernate Models objects.
///
/// @author Steve Ebersole
public class ModelObjectInputStream extends ObjectInputStream {
	private final RestoredModels restoredModels;

	public ModelObjectInputStream(InputStream in, RestoredModels restoredModels) throws IOException {
		super( in );
		if ( restoredModels == null ) {
			throw new IllegalArgumentException( "Restored models cannot be null" );
		}
		this.restoredModels = restoredModels;
		enableResolveObject( true );
	}

	@Override
	protected Object resolveObject(Object object) throws IOException {
		if ( object instanceof ModelReferenceProxy proxy ) {
			return restoredModels.resolve( proxy.reference() );
		}
		return super.resolveObject( object );
	}
}
