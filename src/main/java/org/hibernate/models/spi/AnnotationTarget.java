/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
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

/**
 * Abstract for something where an annotation can be {@linkplain AnnotationUsage used}.
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
	 * Access to all the annotations used on this target.
	 *
	 * @apiNote This returns the usages directly available on the target; it does not
	 * expand repeatable containers (e.g. NamedQueries -> *NamedQuery).
	 */
	Collection<AnnotationUsage<?>> getAllAnnotationUsages();

	/**
	 * Allows to visit every annotation on the target.
	 *
	 * @apiNote Only visits the usages directly available on the target; it does not
	 * visit across repeatable containers (e.g. NamedQueries -> *NamedQuery).
	 */
	default void forAllAnnotationUsages(Consumer<AnnotationUsage<?>> consumer) {
		getAllAnnotationUsages().forEach( consumer );
	}

	/**
	 * Whether the given annotation is used on this target.
	 *
	 * @see #hasRepeatableAnnotationUsage
	 *
	 * @apiNote This form does not check across repeatable containers.  E.g., calling this
	 * method with {@code NamedQuery} will return {@code false} when the target directly
	 * has a NamedQueries.
	 */
	<A extends Annotation> boolean hasAnnotationUsage(Class<A> type);

	/**
	 * Whether the given annotation is used on this target.
	 *
	 * @see #hasAnnotationUsage
	 *
	 * @apiNote This forms does check across repeatable containers.  E.g., calling this
	 * method with {@code NamedQuery} will return {@code true} when the target directly
	 * has a NamedQueries.
	 */
	<A extends Annotation> boolean hasRepeatableAnnotationUsage(Class<A> type);

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
	 * For also checking across meta-annotations, see {@linkplain #locateAnnotationUsage(Class)}.
	 *
	 * @return The usage or {@code null}
	 */
	<A extends Annotation> AnnotationUsage<A> getAnnotationUsage(AnnotationDescriptor<A> descriptor);

	/**
	 * Form of {@link #getAnnotationUsage(AnnotationDescriptor)} accepting the annotation {@linkplain Class}
	 */
	<A extends Annotation> AnnotationUsage<A> getAnnotationUsage(Class<A> type);

	/**
	 * Form of {@linkplain #getAnnotationUsage(AnnotationDescriptor)} which returns {@code null} instead of
	 * throwing {@linkplain AnnotationAccessException} when more than one usage of the requested
	 * annotation exists.
	 */
	default <A extends Annotation> AnnotationUsage<A> getSingleAnnotationUsage(AnnotationDescriptor<A> descriptor) {
		try {
			return getAnnotationUsage( descriptor );
		}
		catch (AnnotationAccessException ignore) {
			return null;
		}
	}

	/**
	 * Form of {@link #getSingleAnnotationUsage(AnnotationDescriptor)} accepting the annotation {@linkplain Class}
	 */
	default <A extends Annotation> AnnotationUsage<A> getSingleAnnotationUsage(Class<A> type) {
		try {
			return getAnnotationUsage( type );
		}
		catch (AnnotationAccessException ignore) {
			return null;
		}
	}

	/**
	 * Form of {@linkplain #getAnnotationUsage} which also considers meta-annotations -
	 * annotations on the classes of each {@linkplain #getAllAnnotationUsages() local annotation}.
	 */
	<A extends Annotation> AnnotationUsage<A> locateAnnotationUsage(Class<A> type);

	/**
	 * Get all usages of the specified {@code annotationType} in this scope.
	 *
	 * @apiNote For {@linkplain Repeatable repeatable} annotation types (e.g. {@code @NamedQuery}),
	 * the returned list will contain the union of <ol>
	 *     <li>the singular {@code @NamedQuery} usage</li>
	 *     <li>the nested {@code @NamedQuery} usages from the {@code @NamedQueries} usage</li>
	 * </ol>
	 */
	<A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(AnnotationDescriptor<A> type);

	/**
	 * Form of {@linkplain #getRepeatedAnnotationUsages(AnnotationDescriptor)} accepting the annotation {@linkplain Class}
	 */
	<A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(Class<A> type);

	/**
	 * Call the {@code consumer} for each {@linkplain AnnotationUsage usage} of the
	 * given {@code type}.
	 *
	 * @apiNote For {@linkplain Repeatable repeatable} annotation types, the consumer will also be
	 * called for those defined on the container.
	 */
	default <X extends Annotation> void forEachAnnotationUsage(
			AnnotationDescriptor<X> type,
			Consumer<AnnotationUsage<X>> consumer) {
		final List<AnnotationUsage<X>> annotations = getRepeatedAnnotationUsages( type );
		if ( annotations == null ) {
			return;
		}
		annotations.forEach( consumer );
	}

	/**
	 * Form of {@link #forEachAnnotationUsage(AnnotationDescriptor, Consumer)} accepting the annotation {@linkplain Class}
	 */
	<X extends Annotation> void forEachAnnotationUsage(Class<X> type, Consumer<AnnotationUsage<X>> consumer);

	/**
	 * Returns all AnnotationUsage references from this target where the usage's
	 * {@linkplain AnnotationUsage#getAnnotationDescriptor() annotation class} is annotated
	 * with the given {@code metaAnnotationType}.
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
	<A extends Annotation> List<AnnotationUsage<? extends Annotation>> getMetaAnnotated(Class<A> metaAnnotationType);

	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 */
	default <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName) {
		return getNamedAnnotationUsage( type, matchName, "name" );
	}

	/**
	 * Helper form of {@linkplain #getNamedAnnotationUsage(AnnotationDescriptor, String)}
	 */
	default <X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			Class<X> type,
			String matchName) {
		return getNamedAnnotationUsage( type, matchName, "name" );
	}

	/**
	 * Get a usage of the given annotation {@code type} whose {@code attributeToMatch} attribute value
	 * matches the given {@code matchName}.
	 *
	 * @param matchName The name to match.
	 * @param attributeToMatch Name of the attribute to match on.
	 */
	<X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			AnnotationDescriptor<X> type,
			String matchName,
			String attributeToMatch);

	/**
	 * Helper form of {@linkplain #getNamedAnnotationUsage(AnnotationDescriptor, String, String)}
	 */
	<X extends Annotation> AnnotationUsage<X> getNamedAnnotationUsage(
			Class<X> type,
			String matchName,
			String attributeToMatch);

	/**
	 * Functional contract to process an annotation and return a value.
	 *
	 * @param <T> The type of the value being returned.
	 */
	@FunctionalInterface
	interface AnnotationUsageProcessor<T> {
		/**
		 * The processed value.  May be {@code null} to indicate a "no match"
		 */
		T process(AnnotationUsage<? extends Annotation> annotationUsage);
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
			AnnotationUsageProcessor<T> processor) {
		final List<AnnotationUsage<A>> annotationUsages = getRepeatedAnnotationUsages( annotationType );
		for ( AnnotationUsage<A> annotationUsage : annotationUsages ) {
			final T result = processor.process( annotationUsage );
			if ( result != null ) {
				return result;
			}
		}
		return null;
	}

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
