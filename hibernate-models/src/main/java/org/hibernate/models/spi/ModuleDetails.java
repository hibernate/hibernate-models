/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/// Models a Java module while processing annotations.
///
/// A module is a root annotation target.  Unlike a class, member, or package,
/// it does not have a containing target in the models hierarchy.
///
/// @since 1.3
/// @author Steve Ebersole
public interface ModuleDetails extends AnnotationTarget {
	@Nonnull
	@Override
	default Kind getKind() {
		return Kind.MODULE;
	}

	/// The Java module name.
	///
	/// @return The module name, or `null` for an unnamed module
	@Nullable
	String getModuleName();

	@Nullable
	@Override
	default String getName() {
		return getModuleName();
	}

	@Nullable
	@Override
	default ClassDetails getContainer(@Nonnull ModelsContext modelsContext) {
		return null;
	}

	@Nonnull
	@Override
	default ModuleDetails asModuleDetails() {
		return this;
	}
}
