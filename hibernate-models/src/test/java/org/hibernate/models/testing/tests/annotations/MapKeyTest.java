/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import java.util.Date;
import java.util.Map;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MapKeyClass;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyTemporal;
import jakarta.persistence.OneToMany;
import jakarta.persistence.TemporalType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

public class MapKeyTest {

	@Test
	void testFieldsAreResolved() {
		final ModelsContext modelsContext = createModelContext(
				School.class,
				Person.class
		);

		final ClassDetails schoolClassDetails = modelsContext.getClassDetailsRegistry().getClassDetails( School.class.getName() );

		final FieldDetails idField = schoolClassDetails.findFieldByName( "id" );
		final TypeDetails idFieldType = idField.getType();
		assertThat( idFieldType.isResolved() ).isTrue();

		FieldDetails mapKeyField = schoolClassDetails.findFieldByName( "studentsByDate" );
		TypeDetails typeDetails = mapKeyField.resolveRelativeType( schoolClassDetails );
		assertThat( typeDetails.isResolved() ).isTrue();

		FieldDetails mapKeyField2 = schoolClassDetails.findFieldByName( "teachersByDate" );
		TypeDetails typeDetails2 = mapKeyField2.resolveRelativeType( schoolClassDetails );
		assertThat( typeDetails2.isResolved() ).isFalse();
	}

	@SuppressWarnings({ "unused", "deprecation" })
	@Entity
	public static class School<T> {
		@Id
		private int id;

		@OneToMany(mappedBy = "school")
		@MapKeyClass(Date.class)
		@MapKeyColumn(name = "THE_DATE")
		@MapKeyTemporal(TemporalType.DATE)
		private Map<Object, Person> studentsByDate;

		@OneToMany(mappedBy = "school")
		@MapKeyClass(Date.class)
		@MapKeyColumn(name = "THE_DATE")
		@MapKeyTemporal(TemporalType.DATE)
		private Map<T, Person> teachersByDate;
	}

	@Entity
	public static class Person {
		@Id
		private int id;
	}
}
