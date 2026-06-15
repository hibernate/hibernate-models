/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import org.hibernate.models.internal.ModuleDetailsSupport;
import org.hibernate.models.spi.ModelsContext;

/// ModuleDetails implementation based on a [Module] reference.
///
/// Annotation usages are read lazily from [Module#getAnnotations()] and
/// converted through the owning models context.
///
/// @since 1.3
/// @author Steve Ebersole
public class JdkModuleDetails extends AbstractJdkAnnotationTarget implements ModuleDetailsSupport {
	private final Module module;

	/// Constructs module details for a loaded Java module.
	///
	/// @param module The Java module
	/// @param modelContext The owning models context
	public JdkModuleDetails(Module module, ModelsContext modelContext) {
		super( module::getAnnotations, modelContext );
		this.module = module;
	}

	@Override
	public String getModuleName() {
		return module.getName();
	}

	/// Access to the underlying Java module.
	///
	/// @return The Java module
	public Module toJavaModule() {
		return module;
	}
}
