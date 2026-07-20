/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.hibernate.models.serial.spi.ModelsArchiveWriter;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ConstructorDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModuleDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.TypeDetails;

/// Object stream used by an owning serialization process to replace live
/// Hibernate Models objects with archive references.
///
/// @see org.hibernate.models.serial.spi.ModelsArchives#createObjectOutputStream
///
/// @author Steve Ebersole
public class ModelObjectOutputStream extends ObjectOutputStream {
	private final ModelsArchiveWriter archiveWriter;

	public ModelObjectOutputStream(OutputStream out, ModelsArchiveWriter archiveWriter) throws IOException {
		super( out );
		if ( archiveWriter == null ) {
			throw new IllegalArgumentException( "Models archive writer cannot be null" );
		}
		this.archiveWriter = archiveWriter;
		enableReplaceObject( true );
	}

	@Override
	protected Object replaceObject(Object object) throws IOException {
		if ( object instanceof ClassDetails classDetails ) {
			return new ModelReferenceProxy( archiveWriter.reference( classDetails ) );
		}
		if ( object instanceof TypeDetails typeDetails ) {
			return new ModelReferenceProxy( archiveWriter.reference( typeDetails ) );
		}
		if ( object instanceof FieldDetails fieldDetails ) {
			return new ModelReferenceProxy( archiveWriter.reference( fieldDetails ) );
		}
		if ( object instanceof MethodDetails methodDetails ) {
			return new ModelReferenceProxy( archiveWriter.reference( methodDetails ) );
		}
		if ( object instanceof ConstructorDetails constructorDetails ) {
			return new ModelReferenceProxy( archiveWriter.reference( constructorDetails ) );
		}
		if ( object instanceof RecordComponentDetails recordComponentDetails ) {
			return new ModelReferenceProxy( archiveWriter.reference( recordComponentDetails ) );
		}
		if ( object instanceof ModuleDetails moduleDetails ) {
			return new ModelReferenceProxy( archiveWriter.reference( moduleDetails ) );
		}
		return super.replaceObject( object );
	}
}
