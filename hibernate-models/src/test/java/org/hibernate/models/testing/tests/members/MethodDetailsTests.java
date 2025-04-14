/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.members;

import java.lang.reflect.Method;

import org.hibernate.models.internal.jdk.JdkMethodDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MemberDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests for modeling methods
 *
 * @author Steve Ebersole
 */
public class MethodDetailsTests {
	@Test
	void testMethods() {
		final ModelsContext modelsContext = createModelContext( RandomClass.class );

		final ClassDetails classDetails = modelsContext
				.getClassDetailsRegistry()
				.findClassDetails( RandomClass.class.getName() );
		assertThat( classDetails ).isNotNull();

		classDetails.forEachMethod( (pos, method) -> {
			assertThat( method.getDeclaringType() ).isSameAs( classDetails );

			if ( method.getName().equals( "getProperty" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.toJavaMember() ).isInstanceOf( Method.class );
				assertThat( method.toJavaMember().getName() ).isEqualTo( "getProperty" );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "property" );
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
				assertThat( method.isPersistable() ).isTrue();
			}
			else if ( method.getName().equals( "setProperty" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.SETTER );
				assertThat( method.getReturnType() ).isEqualTo( ClassDetails.VOID_CLASS_DETAILS );
				assertThat( method.getArgumentTypes() ).hasSize( 1 );
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
				assertThat( method.isPersistable() ).isFalse();
			}
			else if ( method.getName().equals( "nothing" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.OTHER );
				assertThat( method.getReturnType() ).isEqualTo( ClassDetails.VOID_CLASS_DETAILS );
				assertThat( method.getType() ).isNull();
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
				assertThat( method.isPersistable() ).isFalse();
			}
			else if ( method.getName().equals( "something" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.OTHER );
				assertThat( method.getReturnType() ).isEqualTo( ClassDetails.VOID_CLASS_DETAILS );
				assertThat( method.getType() ).isNull();
				assertThat( method.getArgumentTypes() ).hasSize( 1 );
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
				assertThat( method.isPersistable() ).isFalse();
			}
			else if ( method.getName().equals( "isActive" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( boolean.class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "active" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PRIVATE );
			}
			else if ( method.getName().equals( "getByteValue" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( byte.class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "byteValue" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PROTECTED );
			}
			else if ( method.getName().equals( "getShortValue" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( short.class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "shortValue" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PACKAGE );
			}
			else if ( method.getName().equals( "getIntValue" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( int.class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "intValue" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
			}
			else if ( method.getName().equals( "getLongValue" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( long.class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "longValue" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
			}
			else if ( method.getName().equals( "getDoubleValue" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( double.class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "doubleValue" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
			}
			else if ( method.getName().equals( "getFloatValue" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asPrimitiveType().getClassDetails().toJavaClass() ).isEqualTo( float.class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "floatValue" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
			}
			else if ( method.getName().equals( "getIntArrayValue" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.GETTER );
				assertThat( method.getType().asArrayType().getArrayClassDetails().toJavaClass() ).isEqualTo( int[].class );
				assertThat( method.getArgumentTypes() ).isEmpty();
				assertThat( method.resolveAttributeName() ).isEqualTo( "intArrayValue" );
				assertThat( method.isPersistable() ).isTrue();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
			}
			else if ( method.getName().equals( "somethingStatic" ) ) {
				assertThat( method.getMethodKind() ).isEqualTo( MethodDetails.MethodKind.OTHER );
				assertThat( method.isPersistable() ).isFalse();
				assertThat( method.getVisibility() ).isEqualTo( MemberDetails.Visibility.PUBLIC );
			}
			else {
				//noinspection StatementWithEmptyBody
				if ( ( (JdkMethodDetails) method ).getMethod().isSynthetic() ) {
					// ignore it
				}
				else {
					fail();
				}
			}
		} );
	}


	@SuppressWarnings("unused")
	public static class RandomClass {
		public Integer getProperty() { return null; }
		public void setProperty(Integer id) {}

		private boolean isActive() { return true; }
		protected byte getByteValue() { return 0; }
		short getShortValue() { return 0; }
		public int getIntValue() { return 0; }
		public long getLongValue() { return 0; }
		public double getDoubleValue() { return 0; }
		public float getFloatValue() { return 0; }
		public int[] getIntArrayValue() { return null; }

		public void nothing() {}
		public void something(Object stuff) {}

		public static void somethingStatic() {}
	}
}
