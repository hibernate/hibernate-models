/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.internal.ModuleDetailsSupport;

import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.module.ModuleDescription;

/// ModuleDetails implementation based on a Byte Buddy module description.
///
/// Annotation usages are read lazily from the [ModuleDescription] through
/// the common Byte Buddy annotation conversion support.
///
/// @since 1.3
/// @author Steve Ebersole
public class ModuleDetailsImpl extends AbstractAnnotationTarget implements ModuleDetailsSupport {
	private final ModuleDescription moduleDescription;

	/// Constructs module details for a Byte Buddy module description.
	///
	/// @param moduleDescription The Byte Buddy module description
	/// @param modelContext The owning models context
	public ModuleDetailsImpl(
			ModuleDescription moduleDescription,
			ByteBuddyModelsContext modelContext) {
		super( modelContext );
		this.moduleDescription = moduleDescription;
	}

	@Override
	protected AnnotationSource getAnnotationSource() {
		return moduleDescription;
	}

	@Override
	public String getModuleName() {
		return moduleDescription.getActualName();
	}
}
