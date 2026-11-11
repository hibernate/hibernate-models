/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Target;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.IllegalCastException;
import org.hibernate.models.internal.AnnotationTargetHelper;

/**
 * Abstract for something where an annotation can be used.
 *
 * @see java.lang.reflect.AnnotatedElement
 *
 * @author Steve Ebersole
 */
public interface AnnotationTarget {
	/**
	 * The kind of target
	 */
	@Nonnull
	Kind getKind();

	/**
	 * A descriptive name for the target used mostly for logging.
	 * May be {@code null} for an unnamed module.
	 */
	@Nullable
	String getName();

	/**
	 * Get the ClassDetails for the class which is the "container" for this target.<ul>
	 *     <li>For a member ({@code org.hib.Thing#id}), this will be the declaring type ({@code org.hib.Thing}).
	 *     <li>For a class ({@code org.hib.Thing}), this will be the ClassDetails for its package ({@code org.hib.package-info})
	 *     <li>For a package ({@code org.hib.package-info}), this will be the ClassDetails for the containing package ({@code org.package-info})
	 * </ul>
	 *
	 * @apiNote If not already, this resolution should be registered into the context's
	 * {@linkplain ModelsContext#getClassDetailsRegistry() class registry}
	 */
	@Nullable
	ClassDetails getContainer(@Nonnull ModelsContext modelsContext);

	/**
	 * Walk "up" {@linkplain #getContainer containers} from this target.
	 *
	 * @param crossPackageBoundaries If this target is a package, should we walk to its container?
	 * @param modelContext Access to useful stuff.
	 * @param consumer Consumer of the containers
	 */
	default void walkContainers(
			boolean crossPackageBoundaries,
			@Nonnull ModelsContext modelContext,
			@Nonnull Consumer<ClassDetails> consumer) {
		AnnotationTargetHelper.walkContainers( this, crossPackageBoundaries, modelContext, consumer );
	}

	/**
	 * Walk "up" {@linkplain #getContainer containers} from this target.
	 *
	 * @param crossPackageBoundaries If this target is a package, should we walk to its container?
	 * @param modelContext Access to useful stuff.
	 * @param consumer Consumer of the containers
	 */
	default void walkSelfAndContainers(
			boolean crossPackageBoundaries,
			@Nonnull ModelsContext modelContext,
			@Nonnull Consumer<AnnotationTarget> consumer) {
		AnnotationTargetHelper.walkSelfAndContainers( this, crossPackageBoundaries, modelContext, consumer );
	}

	/**
	 * Walk "up" {@linkplain #getContainer containers} from this target.
	 *
	 * @param crossPackageBoundaries If this target is a package, should we walk to its container?
	 * @param modelContext Access to useful stuff.
	 * @param matchingExtractor A function that acts as an extractor against a container.  A non-null return indicates a match, which will be the return from this method.
	 */
	@Nullable
	default <T> T fromContainers(
			boolean crossPackageBoundaries,
			@Nonnull ModelsContext modelContext,
			@Nonnull Function<ClassDetails,T> matchingExtractor) {
		return AnnotationTargetHelper.fromContainers( this, crossPackageBoundaries, modelContext, matchingExtractor );
	}

	/**
	 * Walk "up" {@linkplain #getContainer containers} from this target.
	 *
	 * @param crossPackageBoundaries If this target is a package, should we walk to its container?
	 * @param modelContext Access to useful stuff.
	 * @param matchingExtractor A function that acts as an extractor against a container.  A non-null return indicates a match, which will be the return from this method.
	 */
	@Nullable
	default <T> T fromSelfAndContainers(
			boolean crossPackageBoundaries,
			@Nonnull ModelsContext modelContext,
			@Nonnull Function<AnnotationTarget,T> matchingExtractor) {
		return AnnotationTargetHelper.fromSelfAndContainers( this, crossPackageBoundaries, modelContext, matchingExtractor );
	}

	/**
	 * Access to all the annotations used on this target.
	 *
	 * @apiNote This returns the usages directly available on the target; it does not
	 * expand repeatable containers (e.g. NamedQueries -> *NamedQuery).
	 */
	@Nonnull
	Collection<? extends Annotation> getDirectAnnotationUsages();

	/**
	 * Allows to visit every annotation on the target.
	 *
	 * @apiNote Only visits the usages directly available on the target; it does not
	 * visit across repeatable containers (e.g. NamedQueries -> *NamedQuery).
	 */
	default void forEachDirectAnnotationUsage(@Nonnull Consumer<? extends Annotation> consumer) {
		//noinspection unchecked,rawtypes
		getDirectAnnotationUsages().forEach( (Consumer) consumer );
	}

	/**
	 * Whether the given annotation is used on this target.
	 *
	 * @see #hasAnnotationUsage
	 *
	 * @apiNote This form does not check across repeatable containers.  E.g., calling this
	 * method with {@code NamedQuery} will return {@code false} when the target directly
	 * has a NamedQueries.
	 */
	<A extends Annotation> boolean hasDirectAnnotationUsage(@Nonnull Class<A> type);

	/**
	 * Form of {@linkplain #getAnnotationUsage(AnnotationDescriptor, ModelsContext)} which returns {@code null} instead of
	 * throwing {@linkplain AnnotationAccessException} when more than one usage of the requested
	 * annotation exists.
	 */
	@Nullable
	<A extends Annotation> A getDirectAnnotationUsage(@Nonnull AnnotationDescriptor<A> descriptor);

	/**
	 * Form of {@link #getDirectAnnotationUsage(AnnotationDescriptor)} accepting the annotation {@linkplain Class}
	 */
	@Nullable
	<A extends Annotation> A getDirectAnnotationUsage(@Nonnull Class<A> type);

	/**
	 * Whether the given annotation is used on this target.
	 *
	 * @see #hasDirectAnnotationUsage
	 *
	 * @apiNote This forms does check across repeatable containers.  E.g., calling this
	 * method with {@code NamedQuery} will return {@code true} when the target directly
	 * has a NamedQueries.
	 */
	<A extends Annotation> boolean hasAnnotationUsage(@Nonnull Class<A> type, @Nonnull ModelsContext modelContext);

	/**
	 * Get the usage of the given annotation on this target.
	 * <p/>
	 * For {@linkplain Repeatable repeatable} annotation types (e.g. {@code @NamedQuery}), this method will either-<ul>
	 *     <li>
	 *         if a single repeatable annotation itself is present, it is returned.
	 *     </li>
	 *     <li>
	 *         if the {@linkplain Repeatable#value() "containing annotation"} is present (e.g. {@code @NamedQueries}), <ul>
	 *             <li>
	 *                 if the container contains just a single repeatable, that one is returned
	 *             </li>
	 *             <li>
	 *                 if the container contains multiple repeatables, {@link AnnotationAccessException} will be thrown
	 *             </li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 * <p/>
	 * For also checking across meta-annotations, see {@linkplain #locateAnnotationUsage(Class, ModelsContext)}.
	 *
	 * @return The usage or {@code null}
	 */
	@Nullable
	<A extends Annotation> A getAnnotationUsage(@Nonnull AnnotationDescriptor<A> descriptor, @Nonnull ModelsContext modelContext);

	/**
	 * Form of {@link #getAnnotationUsage(AnnotationDescriptor, ModelsContext)} accepting the annotation {@linkplain Class}
	 */
	@Nullable
	default <A extends Annotation> A getAnnotationUsage(@Nonnull Class<A> type, @Nonnull ModelsContext modelContext) {
		return getAnnotationUsage( modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ), modelContext );
	}

	/**
	 * Form of {@linkplain #getAnnotationUsage} which also considers meta-annotations -
	 * annotations on the classes of each {@linkplain #getDirectAnnotationUsages() local annotation}.
	 */
	@Nullable
	<A extends Annotation> A locateAnnotationUsage(@Nonnull Class<A> type, @Nonnull ModelsContext modelContext);

	/**
	 * Get all usages of the specified {@code annotationType} in this scope.
	 *
	 * @return The usages, or {@code null} for simple class details which do not collect annotation usages
	 *
	 * @apiNote For {@linkplain Repeatable repeatable} annotation types (e.g. {@code @NamedQuery}),
	 * the returned list will contain the union of <ol>
	 *     <li>the singular {@code @NamedQuery} usage</li>
	 *     <li>the nested {@code @NamedQuery} usages from the {@code @NamedQueries} usage</li>
	 * </ol>
	 */
	@Nullable
	<A extends Annotation> A[] getRepeatedAnnotationUsages(@Nonnull AnnotationDescriptor<A> type, @Nonnull ModelsContext modelContext);

	/**
	 * Form of {@linkplain #getRepeatedAnnotationUsages(AnnotationDescriptor, ModelsContext)} accepting the annotation {@linkplain Class}
	 */
	@Nullable
	default <A extends Annotation> A[] getRepeatedAnnotationUsages(
			@Nonnull Class<A> type,
			@Nonnull ModelsContext modelContext) {
		return getRepeatedAnnotationUsages( modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ), modelContext );
	}

	<A extends Annotation,C extends Annotation> void forEachRepeatedAnnotationUsages(
			@Nonnull Class<A> repeatable,
			@Nonnull Class<C> container,
			@Nonnull ModelsContext modelContext,
			@Nonnull Consumer<A> consumer);

	default <A extends Annotation,C extends Annotation> void forEachRepeatedAnnotationUsages(
			@Nonnull AnnotationDescriptor<A> repeatable,
			@Nonnull ModelsContext modelContext,
			@Nonnull Consumer<A> consumer) {
		assert repeatable.isRepeatable();
		forEachRepeatedAnnotationUsages(
				repeatable.getAnnotationType(),
				repeatable.getRepeatableContainer().getAnnotationType(),
				modelContext,
				consumer
		);
	}

	/**
	 * Call the {@code consumer} for each usage of the given {@code type}.
	 *
	 * @apiNote For {@linkplain Repeatable repeatable} annotation types, the consumer will also be
	 * called for those defined on the container.
	 */
	default <X extends Annotation> void forEachAnnotationUsage(
			@Nonnull AnnotationDescriptor<X> type,
			@Nonnull ModelsContext modelContext,
			@Nonnull Consumer<X> consumer) {
		final X[] annotations = getRepeatedAnnotationUsages( type, modelContext );
		if ( annotations == null ) {
			return;
		}
		for ( X annotation : annotations ) {
			consumer.accept( annotation );
		}
	}

	/**
	 * Form of {@link #forEachAnnotationUsage(AnnotationDescriptor, ModelsContext, Consumer)} accepting the annotation {@linkplain Class}
	 */
	default <X extends Annotation> void forEachAnnotationUsage(
			@Nonnull Class<X> type,
			@Nonnull ModelsContext modelContext,
			@Nonnull Consumer<X> consumer) {
		forEachAnnotationUsage(
				modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ),
				modelContext,
				consumer
		);
	}

	/**
	 * Returns all Annotation usages from this target where the usage's annotation class
	 * is annotated with the given {@code metaAnnotationType}.
	 * <p/>
	 * E.g., given the following class and annotations
	 * <pre class="brush:java">
	 *     {@code @interface TheMeta} {
	 *         ...
	 *     }
	 *
	 *     {@code @TheMeta(...)}
	 *     {@code @interface TheAnnotation} {
	 *         ...
	 *     }
	 *
	 *     {@code @TheAnnotation}
	 *     {@code class TheClass} {
	 *         ...
	 *     }
	 * </pre>
	 * a call to this method passing {@code TheMeta} on {@code ClassDetails(TheClass)} will return
	 * the usage of {@code @TheAnnotation} on {@code TheClass}.
	 *
	 * @apiNote This method does not check across repeatable containers.  Although the return is a List, we
	 * are functionally wanting just the unique ones.
	 */
	@Nonnull
	<A extends Annotation> List<? extends Annotation> getMetaAnnotated(@Nonnull Class<A> metaAnnotationType, @Nonnull ModelsContext modelContext);

	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 */
	@Nullable
	default <X extends Annotation> X getNamedAnnotationUsage(
			@Nonnull AnnotationDescriptor<X> type,
			@Nonnull String matchName,
			@Nonnull ModelsContext modelContext) {
		return getNamedAnnotationUsage( type, matchName, "name", modelContext );
	}

	/**
	 * Helper form of {@linkplain #getNamedAnnotationUsage(AnnotationDescriptor, String, ModelsContext)}
	 */
	@Nullable
	default <X extends Annotation> X getNamedAnnotationUsage(
			@Nonnull Class<X> type,
			@Nonnull String matchName,
			@Nonnull ModelsContext modelContext) {
		return getNamedAnnotationUsage( type, matchName, "name", modelContext );
	}

	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 * @param attributeToMatch Name of the attribute to match on.
	 */
	@Nullable
	<X extends Annotation> X getNamedAnnotationUsage(
			@Nonnull AnnotationDescriptor<X> type,
			@Nonnull String matchName,
			@Nonnull String attributeToMatch,
			@Nonnull ModelsContext modelContext);

	/**
	 * Helper form of {@linkplain #getNamedAnnotationUsage(AnnotationDescriptor, String, String, ModelsContext)}
	 */
	@Nullable
	<X extends Annotation> X getNamedAnnotationUsage(
			@Nonnull Class<X> type,
			@Nonnull String matchName,
			@Nonnull String attributeToMatch,
			@Nonnull ModelsContext modelContext);

	/**
	 * Functional contract to process an annotation and return a value.
	 *
	 * @param <T> The type of the value being returned.
	 */
	@FunctionalInterface
	interface AnnotationUsageProcessor<T,A extends Annotation> {
		/**
		 * The processed value.  May be {@code null} to indicate a "no match"
		 */
		@Nullable
		T process(@Nonnull A annotationUsage);
	}

	/**
	 * Returns a "matching value" using the passed {@code processor} from the
	 * annotations, of the passed {@code annotationType}, used on the target.
	 *
	 * @apiNote In the case of repeatable annotations, the first usage for which
	 * the passed {@code processor} does not return {@code null} will be returned.
	 *
	 * @return The matching value or {@code null}
	 *
	 * @param <T> The type of the value being returned.
	 * @param <A> The type of annotations to check
	 */
	@Nullable
	default <T, A extends Annotation> T fromAnnotations(
			@Nonnull Class<A> annotationType,
			@Nonnull AnnotationUsageProcessor<T,A> processor,
			@Nonnull ModelsContext modelContext) {
		final A[] annotationUsages = getRepeatedAnnotationUsages( annotationType, modelContext );
		for ( A annotationUsage : annotationUsages ) {
			final T result = processor.process( annotationUsage );
			if ( result != null ) {
				return result;
			}
		}
		return null;
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is an {@linkplain Kind#ANNOTATION annotation}.
	 *
	 * @throws IllegalCastException If the target is not an annotation
	 */
	@Nonnull
	default <A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor() {
		throw new IllegalCastException( getKind() + " cannot be cast to AnnotationDescriptor" );
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#CLASS class}.
	 *
	 * @throws IllegalCastException If the target is not a class
	 */
	@Nonnull
	default ClassDetails asClassDetails() {
		throw new IllegalCastException( getKind() + " cannot be cast to ClassDetails" );
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#CONSTRUCTOR constructor}.
	 *
	 * @throws IllegalCastException If the target is not a constructor
	 */
	@Nonnull
	default ConstructorDetails asConstructorDetails() {
		throw new IllegalCastException( getKind() + " cannot be cast to ConstructorDetails" );
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#FIELD field}, {@linkplain Kind#METHOD method} or {@linkplain Kind#RECORD_COMPONENT record component}.
	 *
	 * @throws IllegalCastException If the target is not a member
	 */
	@Nonnull
	default MemberDetails asMemberDetails() {
		throw new IllegalCastException( getKind() + " cannot be cast to MemberDetails" );
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#FIELD field}.
	 *
	 * @throws IllegalCastException If the target is not a field
	 */
	@Nonnull
	default FieldDetails asFieldDetails() {
		throw new IllegalCastException( getKind() + " cannot be cast to FieldDetails" );
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#METHOD method}.
	 *
	 * @throws IllegalCastException If the target is not a method
	 */
	@Nonnull
	default MethodDetails asMethodDetails() {
		throw new IllegalCastException( getKind() + " cannot be cast to MethodDetails" );
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#RECORD_COMPONENT record component}.
	 *
	 * @throws IllegalCastException If the target is not a record component
	 */
	@Nonnull
	default RecordComponentDetails asRecordComponentDetails() {
		throw new IllegalCastException( getKind() + " cannot be cast to RecordComponentDetails" );
	}

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#MODULE module}.
	 *
	 * @throws IllegalCastException If the target is not a module
	 */
	@Nonnull
	default ModuleDetails asModuleDetails() {
		throw new IllegalCastException( getKind() + " cannot be cast to ModuleDetails" );
	}

	/**
	 * Subset of {@linkplain ElementType annotation targets} supported for mapping annotations
	 */
	enum Kind {
		@Nonnull
		ANNOTATION( ElementType.ANNOTATION_TYPE ),
		@Nonnull
		CLASS( ElementType.TYPE ),
		@Nonnull
		CONSTRUCTOR( ElementType.CONSTRUCTOR ),
		@Nonnull
		FIELD( ElementType.FIELD ),
		@Nonnull
		METHOD( ElementType.METHOD ),
		@Nonnull
		RECORD_COMPONENT( ElementType.RECORD_COMPONENT ),
		@Nonnull
		PACKAGE( ElementType.PACKAGE ),
		@Nonnull
		MODULE( ElementType.MODULE );

		private final ElementType elementType;

		Kind(ElementType elementType) {
			this.elementType = elementType;
		}

		@Nonnull
		public ElementType getCorrespondingElementType() {
			return elementType;
		}

		@Nonnull
		public static EnumSet<Kind> from(@Nullable Target target) {
			if ( target == null ) {
				return EnumSet.allOf( Kind.class );
			}
			return from( target.value() );
		}

		@Nonnull
		public static EnumSet<Kind> from(@Nonnull ElementType[] elementTypes) {
			final EnumSet<Kind> kinds = EnumSet.noneOf( Kind.class );
			final Kind[] values = values();
			for ( int i = 0; i < elementTypes.length; i++ ) {
				for ( int v = 0; v < values.length; v++ ) {
					if ( values[v].getCorrespondingElementType().equals( elementTypes[i] ) ) {
						kinds.add( values[v] );
					}
				}
			}
			return kinds;
		}

		@Nullable
		public static Kind from(@Nonnull ElementType elementType) {
			final Kind[] values = values();
			for ( int i = 0; i < values.length; i++ ) {
				if ( values[i].getCorrespondingElementType().equals( elementType ) ) {
					return values[i];
				}
			}
			return null;
		}
	}
}
