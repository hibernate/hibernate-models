/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import org.hibernate.models.internal.util.IndexedConsumer;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;

/**
 * Specialization of ClassDetails to model both {@code void} and {@code Void}
 *
 * @author Steve Ebersole
 */
public class VoidClassDetails implements ClassDetails {
	/**
	 * Models {@code void}
	 */
	public static VoidClassDetails VOID_CLASS_DETAILS = new VoidClassDetails( void.class );

	/**
	 * Models {@code Void}
	 */
	public static VoidClassDetails VOID_OBJECT_CLASS_DETAILS = new VoidClassDetails( Void.class );

	private final Class<?> voidType;

	private VoidClassDetails(Class<?> voidType) {
		this.voidType = voidType;
	}

	@Override
	public String getName() {
		return getClassName();
	}

	@Override
	public String getClassName() {
		return voidType.getName();
	}

	@Override
	public <X> Class<X> toJavaClass() {
		//noinspection unchecked
		return (Class<X>) voidType;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public ClassDetails getSuperType() {
		return null;
	}

	@Override
	public List<ClassDetails> getImplementedInterfaceTypes() {
		return Collections.emptyList();
	}

	@Override
	public List<FieldDetails> getFields() {
		return Collections.emptyList();
	}

	@Override
	public void forEachField(IndexedConsumer<FieldDetails> consumer) {
	}

	@Override
	public List<MethodDetails> getMethods() {
		return Collections.emptyList();
	}

	@Override
	public void forEachMethod(IndexedConsumer<MethodDetails> consumer) {
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
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(AnnotationDescriptor<A> type) {
		return null;
	}

	@Override
	public <A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(Class<A> type) {
		return null;
	}

	@Override
	public <X extends Annotation> void forEachAnnotationUsage(Class<X> type, Consumer<AnnotationUsage<X>> consumer) {
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
