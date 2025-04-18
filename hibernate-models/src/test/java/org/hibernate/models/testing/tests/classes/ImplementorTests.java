/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.classes;

import java.util.Set;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.buildModelContext;

/**
 * @author Steve Ebersole
 */
public class ImplementorTests {
	@Test
	void testDirectImplementors() {
		final ModelsContext modelsContext = buildModelContext(
				PersonImpl.class,
				CustomerImpl.class,
				EmployeeImpl.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		{
			// Person
			final Set<ClassDetails> directImplementors = classDetailsRegistry
					.getDirectImplementors( Person.class.getName() );
			assertThat( directImplementors ).hasSize( 3 );
			assertThat( directImplementors.stream().map( ClassDetails::getName ) ).contains(
					PersonImpl.class.getName(),
					Customer.class.getName(),
					Employee.class.getName()
			);
		}

		{
			// PersonImpl
			final Set<ClassDetails> directImplementors = classDetailsRegistry
					.getDirectImplementors( PersonImpl.class.getName() );
			assertThat( directImplementors ).isEmpty();
		}

		{
			// Customer
			final Set<ClassDetails> directImplementors = classDetailsRegistry
					.getDirectImplementors( Customer.class.getName() );
			assertThat( directImplementors ).hasSize( 1 );
			assertThat( directImplementors.stream().map( ClassDetails::getName ) ).contains(
					CustomerImpl.class.getName()
			);
		}

		{
			// CustomerImpl
			final Set<ClassDetails> directImplementors = classDetailsRegistry
					.getDirectImplementors( CustomerImpl.class.getName() );
			assertThat( directImplementors ).isEmpty();
		}
	}

	@Test
	void testConcreteTypesFromPerson() {
		final ModelsContext modelsContext = buildModelContext(
				PersonImpl.class,
				CustomerImpl.class,
				EmployeeImpl.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Since Person is an interface, we will get all concrete impls,
		// regardless of whether we say to include base or not (because
		// PersonImpl is abstract):
		//		* CustomerImpl
		//		* EmployeeImpl

		final Set<ClassDetails> concreteTypesWithBase = classDetailsRegistry.findConcreteTypes( Person.class.getName(), true );
		assertThat( concreteTypesWithBase ).hasSize( 2 );
		assertThat( concreteTypesWithBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);

		final Set<ClassDetails> concreteTypesWithoutBase = classDetailsRegistry.findConcreteTypes( Person.class.getName(), false );
		assertThat( concreteTypesWithoutBase ).hasSize( 2 );
		assertThat( concreteTypesWithoutBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);
	}

	@Test
	void testImplementorsFromPerson() {
		final ModelsContext modelsContext = buildModelContext(
				PersonImpl.class,
				CustomerImpl.class,
				EmployeeImpl.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// For implementors, it will matter whether we say to include base or not.
		// Without the base (Person) we have:
		// 		* PersonImpl
		//		* Customer
		//		* CustomerImpl
		//		* Employee
		//		* EmployeeImpl

		final Set<ClassDetails> implementorsWithoutBase = classDetailsRegistry.collectImplementors(
				Person.class.getName(),
				false
		);
		assertThat( implementorsWithoutBase ).hasSize( 5 );
		assertThat( implementorsWithoutBase.stream().map( ClassDetails::getName ) ).contains(
				Customer.class.getName(),
				Employee.class.getName(),
				PersonImpl.class.getName(),
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// including the base adds Person

		final Set<ClassDetails> implementorsWithBase = classDetailsRegistry.collectImplementors(
				Person.class.getName(),
				true
		);
		assertThat( implementorsWithBase ).hasSize( 6 );
		assertThat( implementorsWithBase.stream().map( ClassDetails::getName ) ).contains(
				Person.class.getName(),
				Customer.class.getName(),
				Employee.class.getName(),
				PersonImpl.class.getName(),
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);
	}

	@Test
	void testConcreteTypesFromPersonImpl() {
		final ModelsContext modelsContext = buildModelContext(
				PersonImpl.class,
				CustomerImpl.class,
				EmployeeImpl.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Since PersonImpl is a class and abstract, we will get all concrete impls,
		// regardless of whether we say to include base or not:
		//		* CustomerImpl
		//		* EmployeeImpl

		final Set<ClassDetails> concreteTypesWithoutBase = classDetailsRegistry.findConcreteTypes( PersonImpl.class.getName(), false );
		assertThat( concreteTypesWithoutBase ).hasSize( 2 );
		assertThat( concreteTypesWithoutBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);

		final Set<ClassDetails> concreteTypesWithBase = classDetailsRegistry.findConcreteTypes( PersonImpl.class.getName(), true );
		assertThat( concreteTypesWithBase ).hasSize( 2 );
		assertThat( concreteTypesWithBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);
	}

	@Test
	void testImplementorsFromPersonImpl() {
		final ModelsContext modelsContext = buildModelContext(
				PersonImpl.class,
				CustomerImpl.class,
				EmployeeImpl.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// For implementors, it will matter whether we say to include base or not.
		// Without the base (PersonImpl) we have:
		//		* CustomerImpl
		//		* EmployeeImpl

		final Set<ClassDetails> implementorsWithoutBase = classDetailsRegistry.collectImplementors(
				PersonImpl.class.getName(),
				false
		);
		assertThat( implementorsWithoutBase ).hasSize( 2 );
		assertThat( implementorsWithoutBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// Including the base, we now have all 3:
		//		* PersonImpl
		//		* CustomerImpl
		//		* EmployeeImpl

		final Set<ClassDetails> implementorsWithBase = classDetailsRegistry.collectImplementors(
				PersonImpl.class.getName(),
				true
		);
		assertThat( implementorsWithBase ).hasSize( 3 );
		assertThat( implementorsWithBase.stream().map( ClassDetails::getName ) ).contains(
				PersonImpl.class.getName(),
				CustomerImpl.class.getName(),
				EmployeeImpl.class.getName()
		);
	}

	@Test
	void testConcreteTypesFromCustomer() {
		final ModelsContext modelsContext = buildModelContext(
				PersonImpl.class,
				CustomerImpl.class,
				EmployeeImpl.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final Set<ClassDetails> concreteTypesWithoutBase = classDetailsRegistry
				.findConcreteTypes( Customer.class.getName(), false );
		assertThat( concreteTypesWithoutBase ).hasSize( 1 );
		assertThat( concreteTypesWithoutBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName()
		);

		final Set<ClassDetails> concreteTypesWithBase = classDetailsRegistry.findConcreteTypes(
				Customer.class.getName(),
				true
		);
		assertThat( concreteTypesWithBase ).hasSize( 1 );
		assertThat( concreteTypesWithoutBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName()
		);
	}

	@Test
	void testImplementorsFromCustomer() {
		final ModelsContext modelsContext = buildModelContext(
				PersonImpl.class,
				CustomerImpl.class,
				EmployeeImpl.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final Set<ClassDetails> implementorsWithoutBase = classDetailsRegistry
				.collectImplementors( Customer.class.getName(), false );
		assertThat( implementorsWithoutBase ).hasSize( 1 );
		assertThat( implementorsWithoutBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName()
		);

		final Set<ClassDetails> implementorsWithBase = classDetailsRegistry
				.collectImplementors( Customer.class.getName(), false );
		assertThat( implementorsWithBase ).hasSize( 1 );
		assertThat( implementorsWithBase.stream().map( ClassDetails::getName ) ).contains(
				CustomerImpl.class.getName()
		);
	}
}
