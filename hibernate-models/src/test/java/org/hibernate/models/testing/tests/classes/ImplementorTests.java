/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.classes;

import java.util.Set;

import org.hibernate.models.internal.BasicModelsContextImpl;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.orm.OrmAnnotationHelper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class ImplementorTests {
	public static final String PERSON = Person.class.getName();
	public static final String EMPLOYEE = Employee.class.getName();
	public static final String CUSTOMER = Customer.class.getName();

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testAssumedOrmUsage(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final SingularReference<ClassDetails> singularReference = new SingularReference<>();

		// in ORM use, we will really care about cases where the base is an interface.
		// we will look for a single concrete implementation
		// 		- if we find one great, we will use it
		//		- if not, it would be considered an error (or possibly look for an annotation)

		try {
			classDetailsRegistry.walkConcreteTypes( PERSON, false, singularReference::setReference );
			if ( tracking == Tracking.TRACK ) {
				fail( "Should have failed, as we should have found multiple" );
			}
			else {
				assertThat( singularReference.getReference() ).isNull();
			}
		}
		catch (RuntimeException e) {
			if ( tracking == Tracking.TRACK ) {
				// this is expected
			}
			else {
				throw e;
			}
		}

		singularReference.clear();

		try {
			classDetailsRegistry.walkConcreteTypes( EMPLOYEE, false, singularReference::setReference );
			if ( tracking == Tracking.TRACK ) {
				assertThat( singularReference.getReference() ).isNotNull();
				assertThat( singularReference.getReference().getClassName() ).isEqualTo( EmployeeImpl.class.getName() );
			}
			else {
				assertThat( singularReference.getReference() ).isNull();
			}
		}
		catch (RuntimeException e) {
			throw e;
		}

		singularReference.clear();

		try {
			classDetailsRegistry.walkConcreteTypes( CUSTOMER, false, singularReference::setReference );
			if ( tracking == Tracking.TRACK ) {
				assertThat( singularReference.getReference() ).isNotNull();
				assertThat( singularReference.getReference().getClassName() ).isEqualTo( CustomerImpl.class.getName() );
			}
			else {
				assertThat( singularReference.getReference() ).isNull();
			}
		}
		catch (RuntimeException e) {
			throw e;
		}

	}

	public static class SingularReference<T> {
		private T reference;

		public T getReference() {
			return reference;
		}

		public void setReference(T reference) {
			if ( this.reference != null ) {
				throw new RuntimeException( "Found more than 1" );
			}
			this.reference = reference;
		}

		void clear() {
			reference = null;
		}
	}

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testDirectImplementors(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		{
			// Person
			final Set<ClassDetails> directImplementors = classDetailsRegistry
					.getDirectImplementors( Person.class.getName() );
			if ( tracking == Tracking.TRACK ) {
				assertThat( directImplementors ).hasSize( 3 );
				assertThat( directImplementors.stream().map( ClassDetails::getName ) ).contains(
						PersonImpl.class.getName(),
						Customer.class.getName(),
						Employee.class.getName()
				);
			}
			else {
				assertThat( directImplementors ).isEmpty();
			}
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
			if ( tracking == Tracking.TRACK ) {
				assertThat( directImplementors ).hasSize( 1 );
				assertThat( directImplementors.stream().map( ClassDetails::getName ) ).contains(
						CustomerImpl.class.getName()
				);
			}
			else {
				assertThat( directImplementors ).isEmpty();
			}
		}

		{
			// CustomerImpl
			final Set<ClassDetails> directImplementors = classDetailsRegistry
					.getDirectImplementors( CustomerImpl.class.getName() );
			assertThat( directImplementors ).isEmpty();
		}
	}

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testConcreteTypesFromPerson(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final Set<ClassDetails> concreteTypesWithBase = classDetailsRegistry.findConcreteTypes( Person.class.getName(), true );
		if ( tracking == Tracking.TRACK ) {
			assertThat( concreteTypesWithBase ).hasSize( 2 );
			assertThat( concreteTypesWithBase.stream().map( ClassDetails::getName ) ).contains(
					CustomerImpl.class.getName(),
					EmployeeImpl.class.getName()
			);
		}
		else {
			assertThat( concreteTypesWithBase ).isEmpty();
		}

		final Set<ClassDetails> concreteTypesWithoutBase = classDetailsRegistry.findConcreteTypes( Person.class.getName(), false );
		if ( tracking == Tracking.TRACK ) {
			assertThat( concreteTypesWithoutBase ).hasSize( 2 );
			assertThat( concreteTypesWithoutBase.stream().map( ClassDetails::getName ) ).contains(
					CustomerImpl.class.getName(),
					EmployeeImpl.class.getName()
			);
		}
		else {
			assertThat( concreteTypesWithoutBase ).isEmpty();
		}
	}

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testImplementorsFromPerson(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final Set<ClassDetails> implementorsWithoutBase = classDetailsRegistry.collectImplementors(
				Person.class.getName(),
				false
		);
		if ( tracking == Tracking.TRACK ) {
			assertThat( implementorsWithoutBase ).hasSize( 5 );
			assertThat( implementorsWithoutBase.stream().map( ClassDetails::getName ) ).contains(
					Customer.class.getName(),
					Employee.class.getName(),
					PersonImpl.class.getName(),
					CustomerImpl.class.getName(),
					EmployeeImpl.class.getName()
			);
		}
		else {
			assertThat( implementorsWithoutBase ).isEmpty();
		}

		// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
		// including the base adds Person

		final Set<ClassDetails> implementorsWithBase = classDetailsRegistry.collectImplementors(
				Person.class.getName(),
				true
		);
		if ( tracking == Tracking.TRACK ) {
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
		else {
			assertThat( implementorsWithBase ).hasSize( 1 );
			assertThat( implementorsWithBase.stream().map( ClassDetails::getName ) ).contains(
					Person.class.getName()
			);
		}
	}

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testConcreteTypesFromPersonImpl(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
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

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testImplementorsFromPersonImpl(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
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

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testConcreteTypesFromCustomer(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final Set<ClassDetails> concreteTypesWithoutBase = classDetailsRegistry
				.findConcreteTypes( Customer.class.getName(), false );
		if ( tracking == Tracking.TRACK ) {
			assertThat( concreteTypesWithoutBase ).hasSize( 1 );
			assertThat( concreteTypesWithoutBase.stream().map( ClassDetails::getName ) ).contains(
					CustomerImpl.class.getName()
			);
		}
		else {
			assertThat( concreteTypesWithoutBase ).isEmpty();
		}

		final Set<ClassDetails> concreteTypesWithBase = classDetailsRegistry.findConcreteTypes(
				Customer.class.getName(),
				true
		);
		if ( tracking == Tracking.TRACK ) {
			assertThat( concreteTypesWithBase ).hasSize( 1 );
			assertThat( concreteTypesWithoutBase.stream().map( ClassDetails::getName ) ).contains(
					CustomerImpl.class.getName()
			);
		}
		else {
			assertThat( concreteTypesWithBase ).isEmpty();
		}
	}

	@ParameterizedTest
	@EnumSource(Tracking.class)
	void testImplementorsFromCustomer(Tracking tracking) {
		final ModelsContext modelsContext = buildModelsContext( tracking );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final Set<ClassDetails> implementorsWithoutBase = classDetailsRegistry
				.collectImplementors( Customer.class.getName(), false );
		if ( tracking == Tracking.TRACK ) {
			assertThat( implementorsWithoutBase ).hasSize( 1 );
			assertThat( implementorsWithoutBase.stream().map( ClassDetails::getName ) ).contains(
					CustomerImpl.class.getName()
			);
		}
		else {
			assertThat( implementorsWithoutBase ).isEmpty();
		}

		final Set<ClassDetails> implementorsWithBase = classDetailsRegistry
				.collectImplementors( Customer.class.getName(), false );
		if ( tracking == Tracking.TRACK ) {
			assertThat( implementorsWithBase ).hasSize( 1 );
			assertThat( implementorsWithBase.stream().map( ClassDetails::getName ) ).contains(
					CustomerImpl.class.getName()
			);
		}
		else {
			assertThat( implementorsWithBase ).isEmpty();
		}
	}

	enum Tracking {
		TRACK(true),
		NO_TRACK(false);

		private final boolean track;

		Tracking(boolean track) {
			this.track = track;
		}

		public boolean shouldTrack() {
			return track;
		}
	}

	private ModelsContext buildModelsContext(Tracking tracking) {
		return new BasicModelsContextImpl(
				SIMPLE_CLASS_LOADING,
				tracking.shouldTrack(),
				(contributions, mc) -> {
					OrmAnnotationHelper.forEachOrmAnnotation( contributions::registerAnnotation );
					mc.getClassDetailsRegistry().resolveClassDetails( PersonImpl.class.getName() );
					mc.getClassDetailsRegistry().resolveClassDetails( CustomerImpl.class.getName() );
					mc.getClassDetailsRegistry().resolveClassDetails( EmployeeImpl.class.getName() );
				}
		);
	}
}
