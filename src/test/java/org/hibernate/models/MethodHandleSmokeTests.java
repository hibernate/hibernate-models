/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.orm.TableAnnotation;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class MethodHandleSmokeTests {
	@Test
	void testAnnotationConstructor() throws Throwable {
		final MethodType methodType = MethodType.methodType( void.class, SourceModelBuildingContext.class );
		final MethodHandle constructor = MethodHandles.publicLookup().findConstructor(
				TableAnnotation.class,
				methodType
		);
		final SourceModelBuildingContext context = null;
		constructor.invoke( context );
	}

	@Test
	void testAttributeAccess() throws Throwable {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext();
		final Table usage = JpaAnnotations.TABLE.createUsage( buildingContext );

		final MethodType nameSignature = MethodType.methodType( String.class );
		final MethodHandle nameAccessor = MethodHandles.publicLookup().findVirtual( Table.class, "name", nameSignature );
		final String name = (String) nameAccessor.invoke( usage );
		assertThat( name ).isNotNull();
		assertThat( name ).isEqualTo( "" );

		final MethodType checkSignature = MethodType.methodType( CheckConstraint[].class );
		final MethodHandle checkGetter = MethodHandles.publicLookup().findVirtual( Table.class, "check", checkSignature );
		final CheckConstraint[] check = (CheckConstraint[]) checkGetter.invoke( usage );
		assertThat( check ).isNotNull();
		assertThat( check ).isEmpty();
	}
}
