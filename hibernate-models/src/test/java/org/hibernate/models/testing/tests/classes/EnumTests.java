/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.classes;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.buildModelContext;

/**
 * @author Steve Ebersole
 */
public class EnumTests {
	@Test
	void basicEnumTest() {
		final SourceModelBuildingContext buildingContext = buildModelContext( AnEnum.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails enumClassDetails = classDetailsRegistry.getClassDetails( AnEnum.class.getName() );
		assertThat( enumClassDetails.isEnum() ).isTrue();
		assertThat( enumClassDetails.isRecord() ).isFalse();
		assertThat( enumClassDetails.isInterface() ).isFalse();
		assertThat( enumClassDetails.isAbstract() ).isFalse();
	}
}
