/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import java.lang.reflect.Proxy;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;

import static jakarta.persistence.ConstraintMode.NO_CONSTRAINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Steve Ebersole
 */
public class NestedAnnotationTests {
	public static final String SECOND_TABLE = "second_table";
	public static final String JOIN_COLUMN_NAME = "person_fk";

	@Test
	void testNestedAnnotationsNotProxy() {
		final ModelsContext modelsContext = createModelContext( Person.class );

		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Person.class.getName() );
		final SecondaryTable secondaryTable = classDetails.getDirectAnnotationUsage( SecondaryTable.class );
		assertThat( secondaryTable.name() ).isEqualTo( SECOND_TABLE );

		assertThat( Proxy.isProxyClass( secondaryTable.foreignKey().getClass() ) ).isFalse();
		assertThat( secondaryTable.foreignKey().value() ).isEqualTo( NO_CONSTRAINT );
		assertThat( secondaryTable.foreignKey().options() ).isEqualTo( "stuff" );

		assertThat( secondaryTable.pkJoinColumns() ).hasSize( 1 );
		assertThat( Proxy.isProxyClass( secondaryTable.pkJoinColumns()[0].getClass() ) ).isFalse();
		assertThat( Proxy.isProxyClass( secondaryTable.pkJoinColumns()[0].foreignKey().getClass() ) ).isFalse();
		assertThat( secondaryTable.pkJoinColumns()[0].foreignKey().value() ).isEqualTo( NO_CONSTRAINT );
		assertThat( secondaryTable.pkJoinColumns()[0].foreignKey().options() ).isEqualTo( "things" );
	}

	@SuppressWarnings("unused")
	@Entity(name="Person")
	@Table(name="Person")
	@SecondaryTable( name = SECOND_TABLE,
			foreignKey = @ForeignKey(value = NO_CONSTRAINT, options = "stuff"),
			pkJoinColumns = {
					@PrimaryKeyJoinColumn( name = JOIN_COLUMN_NAME,
							referencedColumnName = "id",
							foreignKey = @ForeignKey(value = NO_CONSTRAINT, options = "things")
					)
			}
	)
	public static class Person {
		@Id
		private Integer id;
		private String name;
	}
}
