/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.classes;

import org.hibernate.models.bytebuddy.SourceModelTestHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import net.bytebuddy.pool.TypePool;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class EnumTests {
	@Test
	void basicEnumTestWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool( AnEnum.class );
		basicEnumTest( typePool );
	}

	@Test
	void basicRootTestWithoutJandex() {
		basicEnumTest( null );
	}

	private void basicEnumTest(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				AnEnum.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails enumClassDetails = classDetailsRegistry.getClassDetails( AnEnum.class.getName() );
		assertThat( enumClassDetails.isEnum() ).isTrue();
		assertThat( enumClassDetails.isRecord() ).isFalse();
		assertThat( enumClassDetails.isInterface() ).isFalse();
		assertThat( enumClassDetails.isAbstract() ).isFalse();
	}
}
