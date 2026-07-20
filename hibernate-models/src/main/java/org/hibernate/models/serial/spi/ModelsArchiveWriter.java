/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.spi;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ConstructorDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModuleDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.TypeDetails;

/// Collects model objects referenced by an owning serialized graph.
///
/// Writers are stateful.  A writer assigns archive-local reference identifiers as
/// model objects are encountered and produces one completed [ModelsArchive]
/// from that collected state.
///
/// @apiNote A writer instance is intended to be used for model objects associated
/// with a single [org.hibernate.models.spi.ModelsContext].  The archive restores
/// into one `ModelsContext`; callers should use a separate writer for each
/// independently serialized model context.
///
/// @since 2.0
/// @author Steve Ebersole
public interface ModelsArchiveWriter {
	/// Adds a class details reference to the archive.
	///
	/// Repeated references to the same logical class resolve to the same
	/// archive-local [ModelReference].  Referencing a class also captures the
	/// class state needed to rebuild it in the restored [ModelsContext].
	///
	/// @param details The class details to reference.
	///
	/// @return The archive-local class reference.
	ModelReference reference(ClassDetails details);

	/// Adds a type details reference to the archive.
	///
	/// The writer captures the transitive type structure, including referenced
	/// classes, argument types, bounds, array constituent types, and owner scopes
	/// as applicable for the concrete [TypeDetails] kind.
	///
	/// @param details The type details to reference.
	///
	/// @return The archive-local type reference.
	ModelReference reference(TypeDetails details);

	/// Adds a field details reference to the archive.
	///
	/// The field is represented by its restored declaring class and field name.
	/// Referencing a field also captures direct annotation usages associated with
	/// the field.
	///
	/// @param details The field details to reference.
	///
	/// @return The archive-local field reference.
	ModelReference reference(FieldDetails details);

	/// Adds a method details reference to the archive.
	///
	/// The method is represented by its restored declaring class, method name,
	/// and argument type names.  Referencing a method also captures direct
	/// annotation usages associated with the method.
	///
	/// @param details The method details to reference.
	///
	/// @return The archive-local method reference.
	ModelReference reference(MethodDetails details);

	/// Adds a constructor details reference to the archive.
	///
	/// The constructor is represented by its restored declaring class and
	/// argument type names.  Referencing a constructor also captures direct
	/// annotation usages associated with the constructor.
	///
	/// @param details The constructor details to reference.
	///
	/// @return The archive-local constructor reference.
	ModelReference reference(ConstructorDetails details);

	/// Adds a record-component details reference to the archive.
	///
	/// The record component is represented by its restored declaring class and
	/// component name.  Referencing a record component also captures direct
	/// annotation usages associated with the component.
	///
	/// @param details The record-component details to reference.
	///
	/// @return The archive-local record-component reference.
	ModelReference reference(RecordComponentDetails details);

	/// Adds a module details reference to the archive.
	///
	/// The method exists so callers and model-aware object streams have a stable
	/// reference path for module details.  Module archive entries are not yet
	/// supported by the initial archive format and implementations may throw an
	/// [UnsupportedOperationException] when this method is called.
	///
	/// @param details The module details to reference.
	///
	/// @return The archive-local module reference.
	ModelReference reference(ModuleDetails details);

	/// Completes archive collection.
	///
	/// After this method returns, the writer is no longer active and further
	/// reference calls are invalid.  The returned [ModelsArchive] contains the
	/// collected model state and may be serialized as part of an owning archive.
	///
	/// @return The completed models archive.
	ModelsArchive finish();
}
