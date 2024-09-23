/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.internal;

import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

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
			SourceModelBuildingContext modelBuildingContext) {
		if ( classDetails.getClassName() == null ) {
			return null;
		}
		final String containingPackageName = determineContainingPackageName( classDetails );
		final String packageInfoClassName = containingPackageName + ".package-info";

		return modelBuildingContext.getClassDetailsRegistry()
				.as( MutableClassDetailsRegistry.class )
				.resolveClassDetails( packageInfoClassName, name -> {
			// see if there is a physical package-info Class
			final Class<Object> packageInfoClass = modelBuildingContext.getClassLoading().findClassForName( packageInfoClassName );
			if ( packageInfoClass == null ) {
				return new MissingPackageInfoDetails( containingPackageName, packageInfoClassName );
			}
			else {
				return new JdkClassDetails( packageInfoClass, modelBuildingContext );
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
}
