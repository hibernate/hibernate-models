/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.internal.util.IndexedConsumer;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;

/**
 * ClassDetails implementation used to represent a {@code package-info} details when
 * there is not a physical {@code package-info} class.
 *
 * @author Steve Ebersole
 */
public record MissingPackageInfoDetails(String packageName, String packageInfoClassName) implements ClassDetails {

	@Override
	public String getName() {
		return packageInfoClassName;
	}

	@Override
	public Collection<? extends Annotation> getDirectAnnotationUsages() {
		return List.of();
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
	public <A extends Annotation> boolean hasAnnotationUsage(Class<A> type, ModelsContext modelContext) {
		return false;
	}

	@Override
	public <A extends Annotation> A getAnnotationUsage(
			AnnotationDescriptor<A> descriptor,
			ModelsContext modelContext) {
		return null;
	}

	@Override
	public <A extends Annotation> A locateAnnotationUsage(Class<A> type, ModelsContext modelContext) {
		return null;
	}

	@Override
	public <A extends Annotation> A[] getRepeatedAnnotationUsages(
			AnnotationDescriptor<A> type,
			ModelsContext modelContext) {
		return null;
	}

	@Override
	public <A extends Annotation, C extends Annotation> void forEachRepeatedAnnotationUsages(
			Class<A> repeatable,
			Class<C> container,
			ModelsContext modelContext,
			Consumer<A> consumer) {

	}

	@Override
	public <A extends Annotation> List<? extends Annotation> getMetaAnnotated(
			Class<A> metaAnnotationType,
			ModelsContext modelContext) {
		return List.of();
	}

	@Override
	public <X extends Annotation> X getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch,
			ModelsContext modelContext) {
		return null;
	}

	@Override
	public <X extends Annotation> X getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch,
			ModelsContext modelContext) {
		return null;
	}

	@Override
	public String getClassName() {
		return packageInfoClassName;
	}

	@Override
	public boolean isResolved() {
		return true;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public boolean isEnum() {
		return false;
	}

	@Override
	public boolean isRecord() {
		return false;
	}

	@Override
	public ClassDetails getSuperClass() {
		return null;
	}

	@Override
	public TypeDetails getGenericSuperType() {
		return null;
	}

	@Override
	public List<TypeDetails> getImplementedInterfaces() {
		return List.of();
	}

	@Override
	public List<TypeVariableDetails> getTypeParameters() {
		return List.of();
	}

	@Override
	public boolean isImplementor(Class<?> checkType) {
		return false;
	}

	@Override
	public List<FieldDetails> getFields() {
		return List.of();
	}

	@Override
	public void forEachField(IndexedConsumer<FieldDetails> consumer) {
	}

	@Override
	public List<MethodDetails> getMethods() {
		return List.of();
	}

	@Override
	public void forEachMethod(IndexedConsumer<MethodDetails> consumer) {
	}

	@Override
	public List<RecordComponentDetails> getRecordComponents() {
		return List.of();
	}

	@Override
	public void forEachRecordComponent(IndexedConsumer<RecordComponentDetails> consumer) {
	}

	@Override
	public <X> Class<X> toJavaClass() {
		throw new UnsupportedOperationException( "Missing package-info [" + packageInfoClassName + "] cannot be converted to a Java Class" );
	}

	@Override
	public <X> Class<X> toJavaClass(ClassLoading classLoading, ModelsContext modelContext) {
		return toJavaClass();
	}

	@Override
	public SerialClassDetails toStorableForm() {
		return new SerialFormImpl( packageName, packageInfoClassName );
	}

	private static class SerialFormImpl implements SerialClassDetails {
		private final String packageName;
		private final String packageInfoClassName;

		public SerialFormImpl(String packageName, String packageInfoClassName) {
			this.packageName = packageName;
			this.packageInfoClassName = packageInfoClassName;
		}

		@Override
		public String getName() {
			return packageName;
		}

		@Override
		public String getClassName() {
			return packageInfoClassName;
		}

		@Override
		public ClassDetails fromStorableForm(ModelsContext context) {
			return new MissingPackageInfoDetails( packageName, packageInfoClassName );
		}
	}
}
