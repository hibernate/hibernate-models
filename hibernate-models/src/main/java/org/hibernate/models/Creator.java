/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.dynamic.DynamicClassDetails;
import org.hibernate.models.dynamic.DynamicFieldDetails;
import org.hibernate.models.jdk.JdkClassDetails;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationHelper;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableAnnotationDescriptor;
import org.hibernate.models.spi.MutableClassDetails;
import org.hibernate.models.spi.MutableMemberDetails;
import org.hibernate.models.spi.StandardAnnotationDescriptor;
import org.hibernate.models.spi.TypeDetails;

import java.lang.annotation.Annotation;
import java.util.EnumSet;

/// Factory for creating useful forms of [AnnotationDescriptor] and [ClassDetails].
///
/// @since 1.3
/// @author Steve Ebersole
public class Creator {

	/// Modifier flags used with [#createDynamicMemberDetails] to signify a persistable attribute.
	public static volatile int DYNAMIC_ATTRIBUTE_MODIFIERS = ModifierUtils.DYNAMIC_ATTRIBUTE_MODIFIERS;

	/// Create an AnnotationDescriptor which does not collect annotations from the given `annotationType`.
	/// The allowable targets and inherited flag are inferred from the annotation type.
	///
	/// @param annotationType The annotation class.
	/// @param mutableAnnotationType The mutable (settable) annotation class.
	@Nonnull
	public static <A extends Annotation, C extends A> MutableAnnotationDescriptor<A,C> createCompleteAnnotationDescriptor(
			@Nonnull Class<A> annotationType,
			@Nonnull Class<C> mutableAnnotationType) {
		return createCompleteAnnotationDescriptor(
				annotationType,
				mutableAnnotationType,
				AnnotationHelper.extractTargets( annotationType ),
				AnnotationHelper.isInherited( annotationType )
		);
	}

	/// Create an AnnotationDescriptor with inferred targets and inheritance and an explicit repeatable container.
	///
	/// @param annotationType The annotation class.
	/// @param mutableAnnotationType The mutable (settable) annotation class.
	/// @param repeatableContainer The repeatable container if `annotationType` is [repeatable][java.lang.annotation.Repeatable]
	@Nonnull
	public static <A extends Annotation, C extends A> MutableAnnotationDescriptor<A,C> createCompleteAnnotationDescriptor(
			@Nonnull Class<A> annotationType,
			@Nonnull Class<C> mutableAnnotationType,
			@Nullable AnnotationDescriptor<?> repeatableContainer) {
		return createCompleteAnnotationDescriptor(
				annotationType,
				mutableAnnotationType,
				AnnotationHelper.extractTargets( annotationType ),
				AnnotationHelper.isInherited( annotationType ),
				repeatableContainer
		);
	}

	/// Create an AnnotationDescriptor which does not collect annotations from the given `annotationType`.
	/// Passes in more than normal AnnotationDescriptor to avoid lookup.
	///
	/// @param annotationType The annotation class.
	/// @param mutableAnnotationType The mutable (settable) annotation class.
	/// @param allowableTargets The allowable targets (TYPE, FIELD, etc.) for the annotation type
	/// @param inherited Whether the annotation is denoted as inheritable.  See [java.lang.annotation.Inherited].
	@Nonnull
	public static <A extends Annotation, C extends A> MutableAnnotationDescriptor<A,C> createCompleteAnnotationDescriptor(
			@Nonnull Class<A> annotationType,
			@Nonnull Class<C> mutableAnnotationType,
			@Nonnull EnumSet<AnnotationTarget.Kind> allowableTargets,
			boolean inherited) {
		return new CompleteAnnotationDescriptor<>( annotationType, mutableAnnotationType, allowableTargets, inherited );
	}

	/// Create an AnnotationDescriptor which does not collect annotations from the given `annotationType`.
	/// Passes in more than normal AnnotationDescriptor to avoid lookup.
	///
	/// @param annotationType The annotation class.
	/// @param mutableAnnotationType The mutable (settable) annotation class.
	/// @param allowableTargets The allowable targets (TYPE, FIELD, etc.) for the annotation type
	/// @param inherited Whether the annotation is denoted as inheritable.  See [java.lang.annotation.Inherited].
	/// @param repeatableContainer The repeatable container if `annotationType` is [repeatable][java.lang.annotation.Repeatable]
	@Nonnull
	public static <A extends Annotation, C extends A> MutableAnnotationDescriptor<A,C> createCompleteAnnotationDescriptor(
			@Nonnull Class<A> annotationType,
			@Nonnull Class<C> mutableAnnotationType,
			@Nonnull EnumSet<AnnotationTarget.Kind> allowableTargets,
			boolean inherited,
			@Nullable AnnotationDescriptor<?> repeatableContainer) {
		return new CompleteAnnotationDescriptor<>( annotationType, mutableAnnotationType, allowableTargets, inherited, repeatableContainer );
	}

	/// Create a standard AnnotationDescriptor.  This form *does* collect annotations from the given `annotationType`.
	///
	/// @param annotationType The annotation class.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static <A extends Annotation> AnnotationDescriptor<A> createAnnotationDescriptor(
			@Nonnull Class<A> annotationType,
			@Nonnull ModelsContext modelsContext) {
		return new StandardAnnotationDescriptor<>( annotationType, modelsContext );
	}

	/// Create a standard AnnotationDescriptor.  This form *does* collect annotations from the given `annotationType`.
	///
	/// @param annotationType The annotation class.
	/// @param repeatableContainer The repeatable container if `annotationType` is [repeatable][java.lang.annotation.Repeatable]
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static <A extends Annotation> AnnotationDescriptor<A> createAnnotationDescriptor(
			@Nonnull Class<A> annotationType,
			@Nullable AnnotationDescriptor<?> repeatableContainer,
			@Nonnull ModelsContext modelsContext) {
		return new StandardAnnotationDescriptor<>( annotationType, repeatableContainer, modelsContext );
	}

	/// Create ClassDetails for a "dynamic" (no physical class) reference.
	///
	/// @param name The dynamic class name.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static MutableClassDetails createDynamicClassDetails(@Nonnull String name, @Nonnull ModelsContext modelsContext) {
		return new DynamicClassDetails( name, modelsContext );
	}

	/// Create ClassDetails for a dynamic class with a supertype.
	///
	/// @param name The dynamic class name.
	/// @param superClass The dynamic class's superclass, or `null`.
	/// @param genericSuperType The dynamic class's generic supertype, or `null`.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static MutableClassDetails createDynamicClassDetails(
			@Nonnull String name,
			@Nullable ClassDetails superClass,
			@Nullable TypeDetails genericSuperType,
			@Nonnull ModelsContext modelsContext) {
		return new DynamicClassDetails( name, superClass, genericSuperType, modelsContext );
	}

	/// Create ClassDetails for a dynamic class with its complete model identity and inheritance details.
	///
	/// @param name The model name.
	/// @param className The Java class name, or `null` for a purely dynamic type.
	/// @param isAbstract Whether the dynamic class is abstract.
	/// @param superClass The dynamic class's superclass, or `null`.
	/// @param genericSuperType The dynamic class's generic supertype, or `null`.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static MutableClassDetails createDynamicClassDetails(
			@Nonnull String name,
			@Nullable String className,
			boolean isAbstract,
			@Nullable ClassDetails superClass,
			@Nullable TypeDetails genericSuperType,
			@Nonnull ModelsContext modelsContext) {
		return new DynamicClassDetails( name, className, isAbstract, superClass, genericSuperType, modelsContext );
	}

	/// Create ClassDetails for a dynamic class with a fallback Java type used when Java class access is requested.
	///
	/// @param name The model name.
	/// @param className The Java class name, or `null` for a purely dynamic type.
	/// @param javaType The fallback Java type, or `null`.
	/// @param isAbstract Whether the dynamic class is abstract.
	/// @param superClass The dynamic class's superclass, or `null`.
	/// @param genericSuperType The dynamic class's generic supertype, or `null`.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static MutableClassDetails createDynamicClassDetails(
			@Nonnull String name,
			@Nullable String className,
			@Nullable Class<?> javaType,
			boolean isAbstract,
			@Nullable ClassDetails superClass,
			@Nullable TypeDetails genericSuperType,
			@Nonnull ModelsContext modelsContext) {
		return new DynamicClassDetails( name, className, javaType, isAbstract, superClass, genericSuperType, modelsContext );
	}

	/// Create member for a [dynamic class][#createDynamicClassDetails].
	///
	/// @param name The member name.
	/// @param type The member type.
	/// @param declaringType The dynamic class.
	/// @param modifierFlags Flags indicating member modifiers (public, private, etc.).
	/// @param isArray Whether the member is an array.
	/// @param isPlural Whether the member is a Java Collection.
	/// @param modelsContext Needed for additional lookups.
	///
	/// @see #DYNAMIC_ATTRIBUTE_MODIFIERS
	@Nonnull
	public static MutableMemberDetails createDynamicMemberDetails(
			@Nonnull String name,
			@Nonnull TypeDetails type,
			@Nonnull ClassDetails declaringType,
			int modifierFlags,
			boolean isArray,
			boolean isPlural,
			@Nonnull ModelsContext modelsContext) {
		return new DynamicFieldDetails( name, type, declaringType, modifierFlags, isArray, isPlural, modelsContext );
	}

	/// Create member for a [dynamic class][#createDynamicClassDetails] using the standard dynamic
	/// [modifiers][#DYNAMIC_ATTRIBUTE_MODIFIERS].
	///
	/// @param name The member name.
	/// @param type The member type.
	/// @param declaringType The dynamic class.
	/// @param isArray Whether the member is an array.
	/// @param isPlural Whether the member is a Java Collection.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static MutableMemberDetails createDynamicMemberDetails(
			@Nonnull String name,
			@Nonnull TypeDetails type,
			@Nonnull ClassDetails declaringType,
			boolean isArray,
			boolean isPlural,
			@Nonnull ModelsContext modelsContext) {
		return new DynamicFieldDetails( name, type, declaringType, DYNAMIC_ATTRIBUTE_MODIFIERS, isArray, isPlural, modelsContext );
	}

	/// Create ClassDetails from a JDK Class reference.
	///
	/// @param managedClass The JDK Class reference.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static MutableClassDetails createJdkClassDetails(@Nonnull Class<?> managedClass, @Nonnull ModelsContext modelsContext) {
		return new JdkClassDetails( managedClass, modelsContext );
	}

	/// Create a named ClassDetails from a JDK Class reference.
	///
	/// @param name The unique name.
	/// @param managedClass The JDK Class reference.
	/// @param modelsContext Needed for additional lookups.
	@Nonnull
	public static MutableClassDetails createJdkClassDetails(@Nonnull String name, @Nonnull Class<?> managedClass, @Nonnull ModelsContext modelsContext) {
		return new JdkClassDetails( name, managedClass, modelsContext );
	}
}
