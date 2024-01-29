/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationUsage;


/**
 * Helper for dealing with annotation wrappers -<ul>
 *     <li>{@link AnnotationDescriptor}</li>
 *     <li>{@link AnnotationUsage}</li>
 * </ul>
 *
 * @see AnnotationHelper
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageHelper {
	public static <A extends Annotation> AnnotationUsage<A> findUsage(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>,AnnotationUsage<? extends Annotation>> usageMap) {
		//noinspection unchecked
		return (AnnotationUsage<A>) usageMap.get( type.getAnnotationType() );
	}

	/**
	 * Get the {@link AnnotationUsage} from the {@code usageMap} for the given {@code type}
	 */
	public static <A extends Annotation> AnnotationUsage<A> getUsage(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>,AnnotationUsage<? extends Annotation>> usageMap) {
		final AnnotationUsage<A> found = findUsage( type, usageMap );
		if ( found == null ) {
			final AnnotationDescriptor<?> repeatableContainer = type.getRepeatableContainer();
			if ( repeatableContainer != null ) {
				final AnnotationUsage<? extends Annotation> containerUsage = findUsage( repeatableContainer, usageMap );
				if ( containerUsage != null ) {
					final List<AnnotationUsage<A>> nestedUsages = containerUsage.getAttributeValue( "value" );
					if ( CollectionHelper.isEmpty( nestedUsages ) ) {
						return null;
					}
					if ( nestedUsages.size() > 1 ) {
						throw new AnnotationAccessException( "Found more than one usage of " + type.getAnnotationType().getName() );
					}
					return nestedUsages.get( 0 );
				}
			}
		}
		return found;
	}

	public static <A extends Annotation> List<AnnotationUsage<A>> getRepeatedUsages(
			AnnotationDescriptor<A> type,
			Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap) {
		// e.g. `@NamedQuery`
		final AnnotationUsage<A> usage = findUsage( type, usageMap );
		// e.g. `@NamedQueries`
		final AnnotationUsage<?> containerUsage = type.getRepeatableContainer() != null
				? findUsage( type.getRepeatableContainer(), usageMap )
				: null;

		if ( containerUsage != null ) {
			final List<AnnotationUsage<A>> repetitions = containerUsage.getAttributeValue( "value" );
			if ( CollectionHelper.isNotEmpty( repetitions ) ) {
				if ( usage != null ) {
					// we can have both when repeatable + inherited are mixed
					final ArrayList<AnnotationUsage<A>> combined = new ArrayList<>( repetitions );
					combined.add( usage );
					return combined;
				}
				return repetitions;
			}
		}

		if ( usage != null ) {
			return Collections.singletonList( usage );
		}

		return Collections.emptyList();
	}

	public static <A extends Annotation> AnnotationUsage<A> getNamedUsage(
			AnnotationDescriptor<A> type,
			String matchValue,
			String attributeToMatch,
			Map<Class<? extends Annotation>, AnnotationUsage<?>> usageMap) {
		final AnnotationUsage<?> annotationUsage = usageMap.get( type.getAnnotationType() );
		if ( annotationUsage != null ) {
			if ( nameMatches( annotationUsage, matchValue, attributeToMatch ) ) {
				//noinspection unchecked
				return (AnnotationUsage<A>) annotationUsage;
			}
			return null;
		}

		final AnnotationDescriptor<?> containerType = type.getRepeatableContainer();
		if ( containerType != null ) {
			final AnnotationUsage<?> containerUsage = usageMap.get( containerType.getAnnotationType() );
			if ( containerUsage != null ) {
				final List<AnnotationUsage<A>> repeatedUsages = containerUsage.getAttributeValue( "value" );
				for ( int i = 0; i < repeatedUsages.size(); i++ ) {
					final AnnotationUsage<A> repeatedUsage = repeatedUsages.get( i );
					if ( nameMatches( repeatedUsage, matchValue, attributeToMatch ) ) {
						return repeatedUsage;
					}
				}
			}
		}

		return null;
	}

	private static boolean nameMatches(AnnotationUsage<?> annotationUsage, String matchValue, String attributeToMatch) {
		final String name = annotationUsage.getAttributeValue( attributeToMatch );
		return matchValue.equals( name );
	}


	private AnnotationUsageHelper() {
	}
}
