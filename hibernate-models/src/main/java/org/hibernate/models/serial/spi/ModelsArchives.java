/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.hibernate.models.serial.internal.ModelObjectInputStream;
import org.hibernate.models.serial.internal.ModelObjectOutputStream;
import org.hibernate.models.serial.internal.ModelsArchiveWriterImpl;

/// Entry point for creating Hibernate Models archives.
///
/// @since 2.0
/// @author Steve Ebersole
public final class ModelsArchives {
	private ModelsArchives() {
	}

	/// Creates a stateful writer for collecting model references and producing a
	/// [ModelsArchive].
	///
	/// The returned writer assigns archive-local identifiers as model objects are
	/// referenced.  Call [ModelsArchiveWriter#finish()] after the owning payload
	/// has been traversed to obtain the completed archive.
	///
	/// @param trackImplementors Whether the restored class-details registry
	/// should track implementors.
	///
	/// @return A new models archive writer.
	///
	/// @apiNote The returned writer is intended to collect references associated
	/// with a single [org.hibernate.models.spi.ModelsContext].  The completed
	/// archive restores into one model context, so callers should create a
	/// separate writer for each independently serialized model context.
	public static ModelsArchiveWriter createWriter(boolean trackImplementors) {
		return new ModelsArchiveWriterImpl( trackImplementors );
	}

	/// Creates an object output stream that replaces live Hibernate Models objects
	/// with model-reference proxies.
	///
	/// This stream is intended for an owning serialization process, such as ORM
	/// metadata serialization, that stores a nested payload alongside the
	/// resulting [ModelsArchive].  As objects are written, encountered model
	/// objects are passed to the supplied [ModelsArchiveWriter] and the payload
	/// receives only serializable model-reference proxies.
	///
	/// @param outputStream The stream that receives the owning serialized payload.
	/// @param archiveWriter The writer that collects referenced model state.
	///
	/// @return An object output stream that performs model-reference replacement.
	///
	/// @throws IOException If the object stream cannot be created.
	public static ObjectOutputStream createObjectOutputStream(
			OutputStream outputStream,
			ModelsArchiveWriter archiveWriter) throws IOException {
		return new ModelObjectOutputStream( outputStream, archiveWriter );
	}

	/// Creates an object input stream that resolves model-reference proxies back
	/// to restored Hibernate Models objects.
	///
	/// The supplied [RestoredModels] should be the result of restoring the
	/// [ModelsArchive] collected while the matching payload was written.
	///
	/// @param inputStream The stream containing the owning serialized payload.
	/// @param restoredModels The restored model context and reference resolver.
	///
	/// @return An object input stream that resolves model-reference proxies.
	///
	/// @throws IOException If the object stream cannot be created.
	public static ObjectInputStream createObjectInputStream(
			InputStream inputStream,
			RestoredModels restoredModels) throws IOException {
		return new ModelObjectInputStream( inputStream, restoredModels );
	}
}
