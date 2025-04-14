/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import org.hibernate.models.internal.ArrayTypeDetailsImpl;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.testing.TestHelper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class MultiDimensionalArrayTypeTests {

	@Test
	void testIntegerArrays() {
		final ModelsContext modelsContext = TestHelper.createModelContext(
				(contributions, ctx) -> {
					final ClassDetails intDetails = new JdkClassDetails( Integer.class, ctx );
					final ClassDetails intArrayDetails = new JdkClassDetails( Integer[].class, ctx );
					final ClassDetails intIntArrayDetails = new JdkClassDetails( Integer[][].class, ctx );
					final ClassDetails intIntIntArrayDetails = new JdkClassDetails( Integer[][][].class, ctx );

					contributions.registerClass( intDetails );
					contributions.registerClass( intArrayDetails );
					contributions.registerClass( intIntArrayDetails );
					contributions.registerClass( intIntIntArrayDetails );
				}
		);

		final ClassDetails intDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Integer[][][].class.getName() );
		final ClassTypeDetailsImpl intTypeDetails = new ClassTypeDetailsImpl( intDetails, TypeDetails.Kind.CLASS );

		final ClassDetails intArrayDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Integer[].class.getName() );
		final ArrayTypeDetailsImpl intArrayTypeDetails = new ArrayTypeDetailsImpl( intArrayDetails, intTypeDetails );

		final ClassDetails intIntArrayDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Integer[][].class.getName() );
		final ArrayTypeDetailsImpl intIntArrayTypeDetails = new ArrayTypeDetailsImpl( intIntArrayDetails, intArrayTypeDetails );

		final ClassDetails intIntIntArrayDetails = modelsContext.getClassDetailsRegistry().getClassDetails( Integer[][][].class.getName() );
		final ArrayTypeDetailsImpl intIntIntArrayTypeDetails = new ArrayTypeDetailsImpl( intIntIntArrayDetails, intIntArrayTypeDetails );

		assertThat( intIntIntArrayTypeDetails.getName() ).isEqualTo( "[[[Ljava.lang.Integer;" );
	}

	@Test
	void testIntArrays() {
		final ModelsContext modelsContext = TestHelper.createModelContext(
				(contributions, ctx) -> {
					final ClassDetails intDetails = new JdkClassDetails( int.class, ctx );
					final ClassDetails intArrayDetails = new JdkClassDetails( int[].class, ctx );
					final ClassDetails intIntArrayDetails = new JdkClassDetails( int[][].class, ctx );
					final ClassDetails intIntIntArrayDetails = new JdkClassDetails( int[][][].class, ctx );

					contributions.registerClass( intDetails );
					contributions.registerClass( intArrayDetails );
					contributions.registerClass( intIntArrayDetails );
					contributions.registerClass( intIntIntArrayDetails );
				}
		);

		final ClassDetails intDetails = modelsContext.getClassDetailsRegistry().getClassDetails( int[][][].class.getName() );
		final ClassTypeDetailsImpl intTypeDetails = new ClassTypeDetailsImpl( intDetails, TypeDetails.Kind.CLASS );

		final ClassDetails intArrayDetails = modelsContext.getClassDetailsRegistry().getClassDetails( int[].class.getName() );
		final ArrayTypeDetailsImpl intArrayTypeDetails = new ArrayTypeDetailsImpl( intArrayDetails, intTypeDetails );

		final ClassDetails intIntArrayDetails = modelsContext.getClassDetailsRegistry().getClassDetails( int[][].class.getName() );
		final ArrayTypeDetailsImpl intIntArrayTypeDetails = new ArrayTypeDetailsImpl( intIntArrayDetails, intArrayTypeDetails );

		final ClassDetails intIntIntArrayDetails = modelsContext.getClassDetailsRegistry().getClassDetails( int[][][].class.getName() );
		final ArrayTypeDetailsImpl intIntIntArrayTypeDetails = new ArrayTypeDetailsImpl( intIntIntArrayDetails, intIntArrayTypeDetails );

		assertThat( intIntIntArrayTypeDetails.getName() ).isEqualTo( "[[[I" );
	}
}
