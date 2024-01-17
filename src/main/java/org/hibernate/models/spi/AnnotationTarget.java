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
 * Abstraction of {@linkplain java.lang.reflect.AnnotatedElement}
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
	 */
	Collection<AnnotationUsage<?>> getAllAnnotationUsages();

	default void forAllAnnotationUsages(Consumer<AnnotationUsage<?>> consumer) {
		getAllAnnotationUsages().forEach( consumer );
	}

	/**
	 * Whether the given annotation is used on this target
	 */
	<A extends Annotation> boolean hasAnnotationUsage(Class<A> type);

	/**
	 * Get the usage of the given annotation on this target.
	 * <p/>
	 * For {@linkplain Repeatable repeatable} annotation types (e.g. {@code @NamedQuery}), this method will either-<ul>
	 *     <li>
	 *         if the repeatable annotation itself is present, it is returned.
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
	 * For annotations which can {@linkplain ElementType#ANNOTATION_TYPE target annotations},
	 * all annotations on this target will be checked as well.
	 *
	 * @return The usage or {@code null}
	 */
	<A extends Annotation> AnnotationUsage<A> getAnnotationUsage(AnnotationDescriptor<A> descriptor);

	/**
	 * Helper form of {@link #getAnnotationUsage(AnnotationDescriptor)}
	 */
	<A extends Annotation> AnnotationUsage<A> getAnnotationUsage(Class<A> type);

	/**
	 * Form of {@linkplain #getAnnotationUsage} which also considers meta-annotations -
	 * annotations on the classes of each {@linkplain #getAllAnnotationUsages() local annotation}.
	 *
	 * E.g., given
	 *
	 * ```java
	 * @Nationalized
	 * @interface SpecialBasic {
	 *     ..
	 * }
	 *
	 * @Entity
	 * class Book {
	 *     ...
	 *     @Basic(..)
	 *     @SpecialBasic(..)
	 *     String isbn;
	 * }
	 * ```
	 *
	 * - getAnnotationUsage(Nationalized.class) -> null
	 * - locateAnnotationUsage(Nationalized.class) -> the one from its @SpecialBasic
	 */
	<A extends Annotation> AnnotationUsage<A> locateAnnotationUsage(Class<A> type);

	/**
	 * Get all usages of the specified {@code annotationType} in this scope.
	 * <p/>
	 * For {@linkplain Repeatable repeatable} annotation types (e.g. {@code @NamedQuery}) -<ul>
	 *     <li>
	 *         if the repeatable annotation itself is present, a singleton list containing that single usage is returned
	 *     </li>
	 *     <li>
	 *         if the {@linkplain Repeatable#value() "containing annotation"} (e.g. {@code @NamedQueries}) is present,
	 *         the contained repeatable usages are extracted from the container and returned as a list
	 *     </li>
	 *     <li>
	 *         Otherwise, an empty list is returned.
	 *     </li>
	 * </ul>
	 *
	 * @apiNote If the passed annotation type is not repeatable, an empty list is returned.
	 */
	<A extends Annotation> List<AnnotationUsage<A>> getRepeatedAnnotationUsages(AnnotationDescriptor<A> type);

	/**
	 * Helper form of {@linkplain #getRepeatedAnnotationUsages(AnnotationDescriptor)}
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
	 * Helper form of {@link #forEachAnnotationUsage(AnnotationDescriptor, Consumer)}
	 */
	<X extends Annotation> void forEachAnnotationUsage(Class<X> type, Consumer<AnnotationUsage<X>> consumer);

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
