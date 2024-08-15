/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.classes;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class EnumTests {
	@Test
	void basicEnumTestWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( AnEnum.class );
		basicEnumTest( index );
	}

	@Test
	void basicRootTestWithoutJandex() {
		basicEnumTest( null );
	}

	private void basicEnumTest(Index index) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
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
