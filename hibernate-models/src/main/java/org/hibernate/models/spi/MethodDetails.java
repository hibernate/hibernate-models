/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

import java.lang.reflect.Method;
import java.util.List;

import org.hibernate.models.UnresolvableMemberException;
import org.hibernate.models.accessor.HibernateAccessorValueReader;
import org.hibernate.models.accessor.HibernateAccessorValueWriter;
import org.hibernate.models.internal.util.StringHelper;

import static org.hibernate.models.internal.ModifierUtils.hasPersistableMethodModifiers;

/**
 * Models a {@linkplain java.lang.reflect.Method method} in a {@linkplain ClassDetails class}.
 *
 * @author Steve Ebersole
 */
public interface MethodDetails extends MemberDetails {
	enum MethodKind {
		GETTER,
		SETTER,
		OTHER
	}

	MethodKind getMethodKind();

	@Override
	default Kind getKind() {
		return Kind.METHOD;
	}

	ClassDetails getReturnType();

	List<ClassDetails> getArgumentTypes();

	@Override
	default boolean isPersistable() {
		return getMethodKind() == MethodKind.GETTER
				&& hasPersistableMethodModifiers( getModifiers() );
	}

	@Override
	default String resolveAttributeName() {
		final String methodName = getName();

		if ( methodName.startsWith( "is" ) ) {
			return StringHelper.decapitalize( methodName.substring( 2 ) );
		}
		else if ( methodName.startsWith( "get" ) ) {
			return StringHelper.decapitalize( methodName.substring( 3 ) );
		}
		else if ( methodName.startsWith( "set" ) ) {
			return StringHelper.decapitalize( methodName.substring( 3 ) );
		}

		return null;
	}

	@Override
	Method toJavaMember();

	@Override
	Method toJavaMember(Class<?> declaringClass, ClassLoading classLoading, ModelsContext modelContext);

	@Override
	default HibernateAccessorValueReader<?> createValueReader() {
		if ( getMethodKind() == MethodKind.SETTER ) {
			throw new UnsupportedOperationException( "Setters cannot be used for a reading operation. Consider using the #resolveValueWriter() instead." );
		}
		if ( getMethodKind() == MethodKind.OTHER ) {
			throw new UnsupportedOperationException("Method '" + getName() + "' is not a getter. Cannot create a value reader.");
		}
		return getModelContext().getAccessorFactory().valueReader( toJavaMember() );
	}

	@Override
	default HibernateAccessorValueReader<?> resolveValueReader() {
		if ( getMethodKind() == MethodKind.SETTER ) {
			// we are looking at a setter... let's find a matching getter
			final String attrName = resolveAttributeName();
			if ( attrName != null ) {
				for ( var method : getDeclaringType().getMethods() ) {
					if ( method.getMethodKind() == MethodKind.GETTER
							&& attrName.equals( method.resolveAttributeName() ) ) {
						return method.createValueReader();
					}
				}
			}
			throw new UnresolvableMemberException("Cannot find a matching getter for setter '" + getName() + "'" );
		}
		return createValueReader();
	}

	@Override
	default HibernateAccessorValueWriter resolveValueWriter() {
		if ( getMethodKind() == MethodKind.GETTER ) {
			// we are looking at a getter... let's find a matching setter
			final String attrName = resolveAttributeName();
			if ( attrName != null ) {
				for ( var method : getDeclaringType().getMethods() ) {
					if ( method.getMethodKind() == MethodKind.SETTER
							&& attrName.equals( method.resolveAttributeName() ) ) {
						return method.createValueWriter();
					}
				}
			}
			throw new UnresolvableMemberException( "Cannot find a matching setter for getter '" + getName() + "'" );
		}
		return createValueWriter();
	}

	@Override
	default HibernateAccessorValueWriter createValueWriter() {
		if ( getMethodKind() == MethodKind.GETTER ) {
			throw new UnsupportedOperationException( "Getters cannot be used for a writing operation. Consider using the #resolveValueReader() instead." );
		}
		if ( getMethodKind() == MethodKind.OTHER ) {
			throw new UnsupportedOperationException("Method '" + getName() + "' is not a setter. Cannot create a value writer.");
		}
		return getModelContext().getAccessorFactory().valueWriter( toJavaMember() );
	}

	@Override
	default MethodDetails asMethodDetails() {
		return this;
	}

}
