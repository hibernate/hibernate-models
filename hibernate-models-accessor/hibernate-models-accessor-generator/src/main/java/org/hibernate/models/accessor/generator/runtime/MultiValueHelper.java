/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.runtime;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.hibernate.models.accessor.HibernateAccessorFactory;
import org.hibernate.models.accessor.HibernateAccessorMultiValueReader;
import org.hibernate.models.accessor.HibernateAccessorMultiValueWriter;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.accessor.spi.MemberValidation;

public final class MultiValueHelper {

	private MultiValueHelper() {
	}

	public static HibernateAccessorMultiValueReader createMultiValueReader(
			HibernateAccessorFactory factory, Class<?> declaringClass, Member[] members) {
		final HibernateAccessorValueReader<?>[] readers = new HibernateAccessorValueReader<?>[members.length];
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateReaderMember( member );
			if ( member instanceof Field field ) {
				readers[i] = factory.valueReader( field );
			}
			else if ( member instanceof Method method ) {
				readers[i] = factory.valueReader( method );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
			}
		}
		return new CompositeMultiValueReader( readers );
	}

	public static HibernateAccessorMultiValueWriter createMultiValueWriter(
			HibernateAccessorFactory factory, Class<?> declaringClass, Member[] members) {
		final HibernateAccessorValueWriter[] writers = new HibernateAccessorValueWriter[members.length];
		for ( int i = 0; i < members.length; i++ ) {
			final Member member = members[i];
			MemberValidation.validateMemberDeclaringType( declaringClass, member );
			MemberValidation.validateWriterMember( member );
			if ( member instanceof Field field ) {
				writers[i] = factory.valueWriter( field );
			}
			else if ( member instanceof Method method ) {
				writers[i] = factory.valueWriter( method );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
			}
		}
		return new CompositeMultiValueWriter( writers );
	}
}
