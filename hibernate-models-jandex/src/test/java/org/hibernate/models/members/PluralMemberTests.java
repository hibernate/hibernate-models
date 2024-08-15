/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.members;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;

/**
 * @author Steve Ebersole
 */
public class PluralMemberTests {
	@Test
	void testWithJandex() {
		verify( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testWithoutJandex() {
		verify( null );
	}

	private void verify(Index index) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				SimpleEntity.class
		);

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		{
			final FieldDetails field = classDetails.findFieldByName( "arrayOfStrings" );
			assertThat( field.isPlural() ).isTrue();
			assertThat( field.isArray() ).isTrue();
		}

		{
			final FieldDetails field = classDetails.findFieldByName( "bagOfStrings" );
			assertThat( field.isPlural() ).isTrue();
			assertThat( field.isArray() ).isFalse();
		}

		{
			final FieldDetails field = classDetails.findFieldByName( "listOfStrings" );
			assertThat( field.isPlural() ).isTrue();
			assertThat( field.isArray() ).isFalse();
		}

		{
			final FieldDetails field = classDetails.findFieldByName( "setOfStrings" );
			assertThat( field.isPlural() ).isTrue();
			assertThat( field.isArray() ).isFalse();
		}

		{
			final FieldDetails field = classDetails.findFieldByName( "mapOfStrings" );
			assertThat( field.isPlural() ).isTrue();
			assertThat( field.isArray() ).isFalse();
		}

		{
			final MethodDetails method = findMethodByName( classDetails, "randomMethod" );
			assertThat( method.getName() ).isEqualTo( "randomMethod" );
			// these are only valid for fields, getters and setters
			assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.OTHER );
			assertThat( method.isPlural() ).isFalse();
			assertThat( method.isArray() ).isFalse();
		}
	}

	private MethodDetails findMethodByName(
			ClassDetails classDetails,
			@SuppressWarnings("SameParameterValue") String methodName) {
		for ( MethodDetails method : classDetails.getMethods() ) {
			if ( method.getName().equals( methodName ) ) {
				return method;
			}
		}
		throw new RuntimeException( "Could not find method named " + methodName );
	}

	@SuppressWarnings({ "unused", "FieldCanBeLocal" })
	@Entity
	public static class SimpleEntity {
		@Id
		@Column(name = "id")
		private Integer id;

		private String name;

		@ElementCollection
		private Collection<String> bagOfStrings;

		@ElementCollection
		private List<String> listOfStrings;

		@ElementCollection
		private Set<String> setOfStrings;

		@ElementCollection
		private Map<String,String> mapOfStrings;

		@ElementCollection
		private String[] arrayOfStrings;

		private SimpleEntity() {
			// for use by Hibernate
		}

		public SimpleEntity(Integer id, String name) {
			this.id = id;
			this.name = name;
		}

		public void randomMethod(String[] stuff) {
		}
	}
}
