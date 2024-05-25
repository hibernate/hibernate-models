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
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

/**
 * ClassDetails implementation for cases where we do not care about fields,
 * methods, record-components nor annotations
 *
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
	public Collection<? extends Annotation> getDirectAnnotationUsages() {
		return Collections.emptyList();
	}

	@Override
	public <A extends Annotation> boolean hasDirectAnnotationUsage(Class<A> type) {
		return false;
	}

	@Override
	public <A extends Annotation> A getDirectAnnotationUsage(AnnotationDescriptor<A> descriptor) {
		return null;
	}

	@Override
	public <A extends Annotation> A getDirectAnnotationUsage(Class<A> type) {
		return null;
	}

	@Override
	public <A extends Annotation> boolean hasAnnotationUsage(Class<A> type, SourceModelBuildingContext modelContext) {
		return false;
	}

	@Override
	public <A extends Annotation> A getAnnotationUsage(
			AnnotationDescriptor<A> descriptor,
			SourceModelBuildingContext modelContext) {
		return null;
	}

	@Override
	public <A extends Annotation> A getAnnotationUsage(Class<A> type, SourceModelBuildingContext modelContext) {
		return null;
	}

	@Override
	public <A extends Annotation> A locateAnnotationUsage(Class<A> type, SourceModelBuildingContext modelContext) {
		return null;
	}

	@Override
	public <A extends Annotation> A[] getRepeatedAnnotationUsages(
			AnnotationDescriptor<A> type,
			SourceModelBuildingContext modelContext) {
		return null;
	}

	@Override
	public <A extends Annotation, C extends Annotation> void forEachRepeatedAnnotationUsages(
			Class<A> repeatable,
			Class<C> container,
			SourceModelBuildingContext modelContext, Consumer<A> consumer) {
	}

	@Override
	public <A extends Annotation> List<? extends Annotation> getMetaAnnotated(
			Class<A> metaAnnotationType,
			SourceModelBuildingContext modelContext) {
		return Collections.emptyList();
	}

	@Override
	public <X extends Annotation> X getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch,
			SourceModelBuildingContext modelContext) {
		return null;
	}

	@Override
	public <X extends Annotation> X getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch,
			SourceModelBuildingContext modelContext) {
		return null;
	}
}
