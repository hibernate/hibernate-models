/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;


/**
 * @author Steve Ebersole
 */
public class SimpleTests {
	@Test
	void testSimple1() {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				Base.class,
				Thing.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( Base.class.getName() );
		classDetails.getFields();
		classDetails.getMethods();
	}

	public static class Base<I> {
		private String[] strings;
		private I[] generics;
	}

	public static class Thing extends Base<Integer> {
	}
}
