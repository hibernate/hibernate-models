/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.hibernate.models.IllegalCastException;
import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.AnnotationProxy;
import org.hibernate.models.serial.internal.SerialAnnotationDescriptorImpl;
import org.hibernate.models.serial.spi.SerialAnnotationDescriptor;
import org.hibernate.models.serial.spi.Storable;

/**
 * Describes an annotation type (the Class)
 *
 * @author Steve Ebersole
 */
public interface AnnotationDescriptor<A extends Annotation>
		extends AnnotationTarget, Storable<AnnotationDescriptor<A>, SerialAnnotationDescriptor<A>> {
	@Override
	default Kind getKind() {
		return Kind.ANNOTATION;
	}

	/**
	 * The annotation type
	 */
	Class<A> getAnnotationType();

	default <S> S as(Class<S> type) {
		//noinspection unchecked
		return (S) type;
	}

	@Override
	default ClassDetails getContainer(SourceModelBuildingContext modelBuildingContext) {
		final ClassDetails annotationClassDetails = modelBuildingContext
				.getClassDetailsRegistry()
				.resolveClassDetails( getAnnotationType().getName() );
		return annotationClassDetails.getContainer( modelBuildingContext );
	}
	/**
	 * Create an empty usage.  Used when there is no source form, such as XML processing.
	 */
	A createUsage(SourceModelBuildingContext context);

	/**
	 * Create a usage from the JDK representation.  This will often just return the passed annotation,
	 * although for Hibernate and JPA annotations we generally want wrappers to be able to manipulate the
	 * values.
	 */
	A createUsage(A jdkAnnotation, SourceModelBuildingContext context);

	/**
	 * Create a usage from the JDK representation.  This will often just return the passed annotation,
	 * although for Hibernate and JPA annotations we generally want wrappers to be able to manipulate the
	 * values.
	 */
	default A createUsage(Map<String, Object> attributeValues, SourceModelBuildingContext context) {
		return AnnotationProxy.makeProxy( this, attributeValues );
	}

	/**
	 * The places the described annotation can be used
	 */
	EnumSet<Kind> getAllowableTargets();

	/**
	 * Whether the annotation defined as {@linkplain java.lang.annotation.Inherited inherited}
	 */
	boolean isInherited();

	/**
	 * Whether the annotation is {@linkplain Repeatable repeatable}.
	 */
	default boolean isRepeatable() {
		return getRepeatableContainer() != null;
	}

	/**
	 * If the described annotation is {@linkplain #isRepeatable repeatable}, returns the descriptor
	 * for the {@linkplain Repeatable#value() container} annotation.
	 */
	AnnotationDescriptor<?> getRepeatableContainer();

	/**
	 * The attributes of the annotation
	 */
	List<AttributeDescriptor<?>> getAttributes();

	/**
	 * Get an attribute descriptor by name, returning {@code null} if the name
	 * is not an attribute of the described annotation.
	 */
	default <V> AttributeDescriptor<V> findAttribute(String name) {
		final List<AttributeDescriptor<?>> attributeDescriptors = getAttributes();
		for ( final AttributeDescriptor<?> attributeDescriptor : attributeDescriptors ) {
			if ( attributeDescriptor.getName().equals( name ) ) {
				//noinspection unchecked
				return (AttributeDescriptor<V>) attributeDescriptor;
			}
		}
		return null;
	}

	/**
	 * Get an attribute descriptor by name.
	 *
	 * @throws UnknownAnnotationAttributeException if the name is not an
	 * attribute of the described annotation.
	 */
	default <V> AttributeDescriptor<V> getAttribute(String name) {
		final AttributeDescriptor<V> attribute = findAttribute( name );
		if ( attribute != null ) {
			return attribute;
		}

		throw new UnknownAnnotationAttributeException( getAnnotationType(), name );
	}

	@Override
	default <X extends Annotation> AnnotationDescriptor<X> asAnnotationDescriptor() {
		//noinspection unchecked
		return (AnnotationDescriptor<X>) this;
	}

	@Override
	default ClassDetails asClassDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to a ClassDetails" );
	}

	@Override
	default MemberDetails asMemberDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to a MemberDetails" );
	}

	@Override
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to a FieldDetails" );
	}

	@Override
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to a MethodDetails" );
	}

	@Override
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( "AnnotationDescriptor cannot be cast to a RecordComponentDetails" );
	}

	@Override
	default SerialAnnotationDescriptor<A> toStorableForm() {
		return new SerialAnnotationDescriptorImpl<>( getAnnotationType() );
	}
}
