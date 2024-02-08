/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.internal.util.IndexedConsumer;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

/**
 * @author Steve Ebersole
 */
public class SimpleClassDetails implements ClassDetails {
	private final Class<?> clazz;
	private final ClassDetails superClassDetails;
	private final TypeDetails genericSuperTypeDetails;

	public SimpleClassDetails(Class<?> clazz) {
		this( clazz, ClassDetails.OBJECT_CLASS_DETAILS, null );
	}

	public SimpleClassDetails(Class<?> clazz, ClassDetails superClassDetails, TypeDetails genericSuperTypeDetails) {
		this.clazz = clazz;
		this.superClassDetails = superClassDetails;
		this.genericSuperTypeDetails = genericSuperTypeDetails;
	}

	@Override
	public String getName() {
		return getClassName();
	}

	@Override
	public String getClassName() {
		return clazz.getName();
	}

	@Override
	public ClassDetails getSuperClass() {
		return superClassDetails;
	}

	@Override
	public TypeDetails getGenericSuperType() {
		return genericSuperTypeDetails;
	}

	@Override
	public <X> Class<X> toJavaClass() {
		//noinspection unchecked
		return (Class<X>) clazz;
	}

	@Override
	public String toString() {
		return "ClassDetails(" + clazz.getName() + ")";
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public boolean isAbstract() {
		return ModifierUtils.isAbstract( clazz.getModifiers() );
	}

	@Override
	public boolean isRecord() {
		return clazz.isRecord();
	}

	@Override
	public List<TypeDetails> getImplementedInterfaces() {
		return Collections.emptyList();
	}

	@Override
	public List<TypeVariableDetails> getTypeParameters() {
		return Collections.emptyList();
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		return checkType.isAssignableFrom( clazz );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// we do not care about the fields, methods nor record components of these simple types

	@Override
	public List<FieldDetails> getFields() {
		// filter out Object's fields
		return Collections.emptyList();
	}

	@Override
	public void forEachField(IndexedConsumer<FieldDetails> consumer) {
	}

	@Override
	public List<MethodDetails> getMethods() {
		// filter out Object's methods
		return Collections.emptyList();
	}

	@Override
	public void forEachMethod(IndexedConsumer<MethodDetails> consumer) {
	}

	@Override
	public List<RecordComponentDetails> getRecordComponents() {
		return Collections.emptyList();
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// nor do we care about its annotations

	@Override
	public Collection<AnnotationUsage<?>> getAllAnnotationUsages() {
		return Collections.emptyList();
	}

	@Override
	public <A extends Annotation> boolean hasAnnotationUsage(Class<A> type) {
		return false;
	}

	@Override
	public <A extends Annotation> boolean hasRepeatableAnnotationUsage(Class<A> type) {
		return false;
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotationUsage(AnnotationDescriptor<A> descriptor) {
		return null;
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> getAnnotationUsage(Class<A> type) {
		return null;
	}

	@Override
	public <A extends Annotation> AnnotationUsage<A> locateAnnotationUsage(Class<A> type) {
		return null;
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(AnnotationDescriptor<A> type) {
		return Collections.emptyList();
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(Class<A> type) {
		return Collections.emptyList();
	}

	@Override
	public <X extends Annotation> void forEachAnnotationUsage(Class<X> type, Consumer<AnnotationUsage<X>> consumer) {
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<? extends Annotation>> getMetaAnnotated(Class<A> metaAnnotationType) {
		return Collections.emptyList();
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch) {
		return null;
	}

	@Override
	public <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch) {
		return null;
	}
}
