/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.function.Consumer;
import java.util.function.Function;

import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;

/**
 * Utilities related to {@linkplain org.hibernate.models.spi.AnnotationTarget}
 *
 * @author Steve Ebersole
 */
public class AnnotationTargetHelper {
	/**
	 * Resolve the ClassDetails descriptor for the package which contains the
	 * given {@code classDetails}.
	 *
	 * @apiNote {@code classDetails} may be the ClassDetails for a `package-info` itself,
	 * in which case this returns the `package-info` ClassDetails for the containing package.
	 */
	public static ClassDetails resolvePackageInfo(
			ClassDetails classDetails,
			ModelsContext modelsContext) {
		if ( classDetails.getClassName() == null ) {
			return null;
		}
		final String containingPackageName = determineContainingPackageName( classDetails );
		if ( containingPackageName == null ) {
			return null;
		}

		final String packageInfoClassName = containingPackageName + ".package-info";

		return modelsContext.getClassDetailsRegistry()
				.as( MutableClassDetailsRegistry.class )
				.resolveClassDetails( packageInfoClassName, name -> {
			// see if there is a physical package-info Class
			final Class<Object> packageInfoClass = modelsContext.getClassLoading().findClassForName( packageInfoClassName );
			if ( packageInfoClass == null ) {
				return new MissingPackageInfoDetails( containingPackageName, packageInfoClassName );
			}
			else {
				return new JdkClassDetails( packageInfoClass, modelsContext );
			}
		} );
	}

	public static String determineContainingPackageName(ClassDetails classDetails) {
		final String className = classDetails.getClassName();
		assert className != null;

		// 2 broad cases here -
		//
		// 		1. we have a "normal" class.
		// 			e.g. given `org.hibernate.FlushMode`, the containing package is `org.hibernate`
		//		2. we have a package-info class
		//			e.g. given `org.hibernate.package-info`, the containing package is really `org`

		// in both cases, this is `org.hibernate`
		final String classNameNamespace = StringHelper.qualifier( className );
		if ( className.endsWith( "package-info" ) ) {
			return classNameNamespace.indexOf( '.' ) > 1
					? StringHelper.qualifier( classNameNamespace )
					: null;
		}
		else {
			return classNameNamespace;
		}
	}

	private AnnotationTargetHelper() {
	}

	public static void walkContainers(
			AnnotationTarget annotationTarget,
			boolean crossPackageBoundaries,
			ModelsContext modelContext,
			Consumer<ClassDetails> consumer) {
		if ( isPackage( annotationTarget ) ) {
			if ( !crossPackageBoundaries ) {
				return;
			}
		}

		final ClassDetails container = annotationTarget.getContainer( modelContext );
		if ( container == null ) {
			return;
		}

		consumer.accept( container );
		container.walkContainers( crossPackageBoundaries, modelContext, consumer );
	}

	@SuppressWarnings("RedundantIfStatement")
	private static boolean isPackage(AnnotationTarget annotationTarget) {
		if ( annotationTarget.getKind() == AnnotationTarget.Kind.PACKAGE ) {
			return true;
		}

		if ( annotationTarget.getKind() == AnnotationTarget.Kind.CLASS
				&& annotationTarget.getName().endsWith( ".package-info" ) ) {
			return true;
		}

		return false;
	}

	public static void walkSelfAndContainers(
			AnnotationTarget self,
			boolean crossPackageBoundaries,
			ModelsContext modelContext,
			Consumer<AnnotationTarget> consumer) {
		if ( self == null ) {
			return;
		}

		consumer.accept( self );
		self.walkContainers( crossPackageBoundaries, modelContext, consumer::accept );
	}

	public static <T> T fromContainers(
			AnnotationTarget annotationTarget,
			boolean crossPackageBoundaries,
			ModelsContext modelContext,
			Function<ClassDetails, T> matchingExtractor) {
		if ( isPackage( annotationTarget ) && !crossPackageBoundaries ) {
			return null;
		}

		final ClassDetails container = annotationTarget.getContainer( modelContext );
		final T matchedExtraction = matchingExtractor.apply( container );
		if ( matchedExtraction != null ) {
			return matchedExtraction;
		}

		return container.fromContainers( crossPackageBoundaries, modelContext, matchingExtractor );
	}

	public static <T> T fromSelfAndContainers(
			AnnotationTarget self,
			boolean crossPackageBoundaries,
			ModelsContext modelContext,
			Function<AnnotationTarget, T> matchingExtractor) {
		if ( self == null ) {
			return null;
		}

		final T fromSelf = matchingExtractor.apply( self );
		if ( fromSelf != null ) {
			return fromSelf;
		}

		return self.fromContainers( crossPackageBoundaries, modelContext, matchingExtractor::apply );
	}
}
