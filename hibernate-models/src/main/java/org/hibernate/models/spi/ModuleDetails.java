/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/// Models a Java module while processing annotations.
///
/// A module is a root annotation target.  Unlike a class, member, or package,
/// it does not have a containing target in the models hierarchy.
///
/// @since 1.3
/// @author Steve Ebersole
public interface ModuleDetails extends AnnotationTarget {
	@Override
	default Kind getKind() {
		return Kind.MODULE;
	}

	/// The Java module name.
	///
	/// @return The module name
	String getModuleName();

	@Override
	default String getName() {
		return getModuleName();
	}

	@Override
	default ClassDetails getContainer(ModelsContext modelsContext) {
		return null;
	}

	@Override
	default ModuleDetails asModuleDetails() {
		return this;
	}
}
