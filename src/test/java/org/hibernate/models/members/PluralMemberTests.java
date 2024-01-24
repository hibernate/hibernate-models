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
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;

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
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
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
	}

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

		public Integer getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Collection<String> getBagOfStrings() {
			return bagOfStrings;
		}

		public void setBagOfStrings(Collection<String> bagOfStrings) {
			this.bagOfStrings = bagOfStrings;
		}

		public List<String> getListOfStrings() {
			return listOfStrings;
		}

		public void setListOfStrings(List<String> listOfStrings) {
			this.listOfStrings = listOfStrings;
		}

		public Set<String> getSetOfStrings() {
			return setOfStrings;
		}

		public void setSetOfStrings(Set<String> setOfStrings) {
			this.setOfStrings = setOfStrings;
		}

		public Map<String, String> getMapOfStrings() {
			return mapOfStrings;
		}

		public void setMapOfStrings(Map<String, String> mapOfStrings) {
			this.mapOfStrings = mapOfStrings;
		}
	}
}
