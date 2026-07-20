/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import org.hibernate.models.spi.ModelsContext;

/// Result of restoring a [ModelsArchive].
///
/// @since 2.0
/// @author Steve Ebersole
public interface RestoredModels {
	/// The model context created while restoring the archive.
	///
	/// All model references resolved from this instance point into this
	/// [ModelsContext].  Owning deserialization processes may retain this context
	/// for later model registry and annotation operations.
	///
	/// @return The restored model context.
	ModelsContext getModelsContext();

	/// Resolves an archive-local model reference.
	///
	/// The returned object corresponds to the reference kind: class, type, field,
	/// method, constructor, record component, or another supported model object.
	/// References must have been produced by the [ModelsArchiveWriter] that
	/// created the restored archive.
	///
	/// @param reference The model reference to resolve.
	///
	/// @return The restored model object.
	Object resolve(ModelReference reference);
}
