/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModuleDetails;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.RecordComponentDetails;

/// Common support for mutable module annotation targets.
///
/// Supplies the safe-cast behavior expected from module targets while inheriting
/// the annotation usage operations from [AnnotationTargetSupport].
///
/// @since 1.3
/// @author Steve Ebersole
public interface ModuleDetailsSupport extends ModuleDetails, AnnotationTargetSupport {
	@Override
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( "ModuleDetails cannot be cast as AnnotationDescriptor" );
	}

	@Override
	default MutableClassDetails asClassDetails() {
		throw new IllegalCastException( "ModuleDetails cannot be cast as ClassDetails" );
	}

	@Override
	default MutableMemberDetails asMemberDetails() {
		throw new IllegalCastException( "ModuleDetails cannot be cast as MemberDetails" );
	}

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "ModuleDetails cannot be cast as FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "ModuleDetails cannot be cast as MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "ModuleDetails cannot be cast as RecordComponentDetails" );
	}
}
