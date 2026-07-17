/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator.runtime;

import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.models.accessor.generator.AccessorClassMetadata.FieldMetadata;
import org.hibernate.models.accessor.generator.AccessorClassMetadata.MemberMetadata;
import org.hibernate.models.accessor.generator.AccessorClassMetadata.MethodMetadata;

public final class NamingUtil {

	private NamingUtil() {
	}

	public static String methodKey(Method method) {
		MethodType mt = MethodType.methodType( method.getReturnType(), method.getParameterTypes() );
		return method.getName() + mt.toMethodDescriptorString();
	}

	public static <T> String constructorDescriptor(java.lang.reflect.Constructor<T> constructor) {
		MethodType mt = MethodType.methodType( void.class, constructor.getParameterTypes() );
		return mt.toMethodDescriptorString();
	}

	public static String multiValueDescriptor(Member... members) {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < members.length; i++ ) {
			if ( i > 0 ) {
				sb.append( ';' );
			}
			Member member = members[i];
			if ( member instanceof Field field ) {
				sb.append( "F:" ).append( field.getDeclaringClass().getName() )
						.append( '.' ).append( field.getName() );
			}
			else if ( member instanceof Method method ) {
				sb.append( "M:" ).append( method.getDeclaringClass().getName() )
						.append( '.' ).append( method.getName() )
						.append( MethodType.methodType( method.getReturnType(), method.getParameterTypes() )
								.toMethodDescriptorString() );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member type: " + member.getClass().getName() );
			}
		}
		return sb.toString();
	}

	public static String multiValueDescriptorFromMetadata(List<? extends MemberMetadata> members) {
		StringBuilder sb = new StringBuilder();
		for ( int i = 0; i < members.size(); i++ ) {
			if ( i > 0 ) {
				sb.append( ';' );
			}
			MemberMetadata member = members.get( i );
			if ( member instanceof FieldMetadata ) {
				sb.append( "F:" ).append( member.declaringClass() )
						.append( '.' ).append( member.name() );
			}
			else if ( member instanceof MethodMetadata ) {
				sb.append( "M:" ).append( member.declaringClass() )
						.append( '.' ).append( member.name() )
						.append( member.descriptor() );
			}
			else {
				throw new IllegalArgumentException( "Unsupported member metadata type: " + member.getClass().getName() );
			}
		}
		return sb.toString();
	}
}
