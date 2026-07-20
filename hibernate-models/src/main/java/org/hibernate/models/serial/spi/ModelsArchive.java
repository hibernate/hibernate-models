/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import java.io.Externalizable;

import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;

/// A versioned, backend-neutral archive of a Hibernate Models graph.
///
/// @since 2.0
/// @author Steve Ebersole
public interface ModelsArchive extends Externalizable {
	/// Restore the archived graph using the supplied runtime dependencies.
	RestoredModels restore(ClassLoading classLoading, RegistryPrimer registryPrimer);
}
