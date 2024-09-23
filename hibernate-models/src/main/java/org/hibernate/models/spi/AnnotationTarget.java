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

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.IllegalCastException;

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
	Kind getKind();

	/**
	 * A descriptive name for the target used mostly for logging
	 */
	String getName();

	/**
	 * Get the ClassDetails for the class which is the "container" for this target.<ul>
	 *     <li>For a member ({@code org.hib.Thing#id}), this will be the declaring type ({@code org.hib.Thing}).
	 *     <li>For a class ({@code org.hib.Thing}), this will be the ClassDetails for its package ({@code org.hib.package-info})
	 *     <li>For a package ({@code org.hib.package-info}), this will be the ClassDetails for the containing package ({@code org.package-info})
	 * </ul>
	 *
	 * @apiNote If not already, this resolution should be registered into the context's
	 * {@linkplain SourceModelBuildingContext#getClassDetailsRegistry() class registry}
	 */
	ClassDetails getContainer(SourceModelBuildingContext modelBuildingContext);

	/**
	 * Access to all the annotations used on this target.
	 *
	 * @apiNote This returns the usages directly available on the target; it does not
	 * expand repeatable containers (e.g. NamedQueries -> *NamedQuery).
	 */
	Collection<? extends Annotation> getDirectAnnotationUsages();

	/**
	 * Allows to visit every annotation on the target.
	 *
	 * @apiNote Only visits the usages directly available on the target; it does not
	 * visit across repeatable containers (e.g. NamedQueries -> *NamedQuery).
	 */
	default void forEachDirectAnnotationUsage(Consumer<? extends Annotation> consumer) {
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
	<A extends Annotation> boolean hasDirectAnnotationUsage(Class<A> type);

	/**
	 * Form of {@linkplain #getAnnotationUsage(AnnotationDescriptor, SourceModelBuildingContext)} which returns {@code null} instead of
	 * throwing {@linkplain AnnotationAccessException} when more than one usage of the requested
	 * annotation exists.
	 */
	<A extends Annotation> A getDirectAnnotationUsage(AnnotationDescriptor<A> descriptor);

	/**
	 * Form of {@link #getDirectAnnotationUsage(AnnotationDescriptor)} accepting the annotation {@linkplain Class}
	 */
	<A extends Annotation> A getDirectAnnotationUsage(Class<A> type);

	/**
	 * Whether the given annotation is used on this target.
	 *
	 * @see #hasDirectAnnotationUsage
	 *
	 * @apiNote This forms does check across repeatable containers.  E.g., calling this
	 * method with {@code NamedQuery} will return {@code true} when the target directly
	 * has a NamedQueries.
	 */
	<A extends Annotation> boolean hasAnnotationUsage(Class<A> type, SourceModelBuildingContext modelContext);

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
	 * For also checking across meta-annotations, see {@linkplain #locateAnnotationUsage(Class, SourceModelBuildingContext)}.
	 *
	 * @return The usage or {@code null}
	 */
	<A extends Annotation> A getAnnotationUsage(AnnotationDescriptor<A> descriptor, SourceModelBuildingContext modelContext);

	/**
	 * Form of {@link #getAnnotationUsage(AnnotationDescriptor, SourceModelBuildingContext)} accepting the annotation {@linkplain Class}
	 */
	default <A extends Annotation> A getAnnotationUsage(Class<A> type, SourceModelBuildingContext modelContext) {
		return getAnnotationUsage( modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ), modelContext );
	}

	/**
	 * Form of {@linkplain #getAnnotationUsage} which also considers meta-annotations -
	 * annotations on the classes of each {@linkplain #getDirectAnnotationUsages() local annotation}.
	 */
	<A extends Annotation> A locateAnnotationUsage(Class<A> type, SourceModelBuildingContext modelContext);

	/**
	 * Get all usages of the specified {@code annotationType} in this scope.
	 *
	 * @apiNote For {@linkplain Repeatable repeatable} annotation types (e.g. {@code @NamedQuery}),
	 * the returned list will contain the union of <ol>
	 *     <li>the singular {@code @NamedQuery} usage</li>
	 *     <li>the nested {@code @NamedQuery} usages from the {@code @NamedQueries} usage</li>
	 * </ol>
	 */
	<A extends Annotation> A[] getRepeatedAnnotationUsages(AnnotationDescriptor<A> type, SourceModelBuildingContext modelContext);

	/**
	 * Form of {@linkplain #getRepeatedAnnotationUsages(AnnotationDescriptor, SourceModelBuildingContext)} accepting the annotation {@linkplain Class}
	 */
	default <A extends Annotation> A[] getRepeatedAnnotationUsages(
			Class<A> type,
			SourceModelBuildingContext modelContext) {
		return getRepeatedAnnotationUsages( modelContext.getAnnotationDescriptorRegistry().getDescriptor( type ), modelContext );
	}

	<A extends Annotation,C extends Annotation> void forEachRepeatedAnnotationUsages(
			Class<A> repeatable,
			Class<C> container,
			SourceModelBuildingContext modelContext,
			Consumer<A> consumer);

	default <A extends Annotation,C extends Annotation> void forEachRepeatedAnnotationUsages(
			AnnotationDescriptor<A> repeatable,
			SourceModelBuildingContext modelContext,
			Consumer<A> consumer) {
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
			AnnotationDescriptor<X> type,
			SourceModelBuildingContext modelContext,
			Consumer<X> consumer) {
		final X[] annotations = getRepeatedAnnotationUsages( type, modelContext );
		if ( annotations == null ) {
			return;
		}
		for ( X annotation : annotations ) {
			consumer.accept( annotation );
		}
	}

	/**
	 * Form of {@link #forEachAnnotationUsage(AnnotationDescriptor, SourceModelBuildingContext, Consumer)} accepting the annotation {@linkplain Class}
	 */
	default <X extends Annotation> void forEachAnnotationUsage(
			Class<X> type,
			SourceModelBuildingContext modelContext,
			Consumer<X> consumer) {
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
	<A extends Annotation> List<? extends Annotation> getMetaAnnotated(Class<A> metaAnnotationType, SourceModelBuildingContext modelContext);

	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 */
	default <X extends Annotation> X getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			SourceModelBuildingContext modelContext) {
		return getNamedAnnotationUsage( type, matchName, "name", modelContext );
	}

	/**
	 * Helper form of {@linkplain #getNamedAnnotationUsage(AnnotationDescriptor, String, SourceModelBuildingContext)}
	 */
	default <X extends Annotation> X getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			SourceModelBuildingContext modelContext) {
		return getNamedAnnotationUsage( type, matchName, "name", modelContext );
	}

	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 * @param attributeToMatch Name of the attribute to match on.
	 */
	<X extends Annotation> X getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch,
			SourceModelBuildingContext modelContext);

	/**
	 * Helper form of {@linkplain #getNamedAnnotationUsage(AnnotationDescriptor, String, String, SourceModelBuildingContext)}
	 */
	<X extends Annotation> X getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch,
			SourceModelBuildingContext modelContext);

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
		T process(A annotationUsage);
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
	default <T, A extends Annotation> T fromAnnotations(
			Class<A> annotationType,
			AnnotationUsageProcessor<T,A> processor,
			SourceModelBuildingContext modelContext) {
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
	<A extends Annotation> AnnotationDescriptor<A> asAnnotationDescriptor();

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#CLASS class}.
	 *
	 * @throws IllegalCastException If the target is not a class
	 */
	ClassDetails asClassDetails();

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#FIELD field}, {@linkplain Kind#METHOD method} or {@linkplain Kind#RECORD_COMPONENT record component}.
	 *
	 * @throws IllegalCastException If the target is not a member
	 */
	MemberDetails asMemberDetails();

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#FIELD field}.
	 *
	 * @throws IllegalCastException If the target is not a field
	 */
	FieldDetails asFieldDetails();

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#METHOD method}.
	 *
	 * @throws IllegalCastException If the target is not a method
	 */
	MethodDetails asMethodDetails();

	/**
	 * Safe cast method for cases when the {@linkplain #getKind() target} is a {@linkplain Kind#RECORD_COMPONENT record component}.
	 *
	 * @throws IllegalCastException If the target is not a record component
	 */
	RecordComponentDetails asRecordComponentDetails();

	/**
	 * Subset of {@linkplain ElementType annotation targets} supported for mapping annotations
	 */
	enum Kind {
		ANNOTATION( ElementType.ANNOTATION_TYPE ),
		CLASS( ElementType.TYPE ),
		FIELD( ElementType.FIELD ),
		METHOD( ElementType.METHOD ),
		RECORD_COMPONENT( ElementType.RECORD_COMPONENT ),
		PACKAGE( ElementType.PACKAGE );

		private final ElementType elementType;

		Kind(ElementType elementType) {
			this.elementType = elementType;
		}

		public ElementType getCorrespondingElementType() {
			return elementType;
		}

		public static EnumSet<Kind> from(Target target) {
			if ( target == null ) {
				return EnumSet.allOf( Kind.class );
			}
			return from( target.value() );
		}

		public static EnumSet<Kind> from(ElementType[] elementTypes) {
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

		public static Kind from(ElementType elementType) {
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
