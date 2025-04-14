/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.internal.PrimitiveKind;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * ClassDetailsBuilder implementation based on {@link Class}
 *
 * @author Steve Ebersole
 */
public class JdkBuilders implements ClassDetailsBuilder, Serializable {
	/**
	 * Singleton access
	 */
	public static final JdkBuilders DEFAULT_BUILDER = new JdkBuilders();

	@Override
	public JdkClassDetails buildClassDetails(String name, ModelsContext modelsContext) {
		return buildClassDetailsStatic( name, modelsContext );
	}

	public static JdkClassDetails buildClassDetailsStatic(String name, ModelsContext modelsContext) {
		if ( char.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( char.class, modelsContext );
		}
		if ( byte.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( byte.class, modelsContext );
		}
		if ( boolean.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( boolean.class, modelsContext );
		}
		if ( short.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( short.class, modelsContext );
		}
		if ( int.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( int.class, modelsContext );
		}
		if ( long.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( long.class, modelsContext );
		}
		if ( float.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( float.class, modelsContext );
		}
		if ( double.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( double.class, modelsContext );
		}
		if ( name.startsWith( "[" ) ) {
			return buildArrayClassDetails( name, modelsContext );
		}

		try {
			final Class<Object> loadedClass = modelsContext.getClassLoading().classForName( name );
			return buildClassDetailsStatic( loadedClass, modelsContext );
		}
		catch (UnknownClassException e) {
			// see if it might be a package name...
			try {
				final Class<Object> packageInfoClass = modelsContext.getClassLoading().classForName( name + ".package-info" );
				return buildClassDetailsStatic( packageInfoClass, modelsContext );
			}
			catch (UnknownClassException noPackage) {
				throw e;
			}
		}
	}

	private static JdkClassDetails buildArrayClassDetails(String name, ModelsContext modelsContext) {
		assert name.startsWith( "[" );
		final int dimensionCount = StringHelper.countArrayDimensions( name );
		assert dimensionCount > 0;

		final String componentTypeName = name.substring( 1 );
		final ClassDetails componentTypeDetails = resolveArrayComponentType( componentTypeName, modelsContext );

		final Class<?> javaClass = componentTypeDetails.toJavaClass();
		final Class<?> arrayType = javaClass.arrayType();
		return new JdkClassDetails( arrayType, modelsContext );
	}

	private static ClassDetails resolveArrayComponentType(String componentTypeName, ModelsContext modelsContext) {
		if ( componentTypeName.startsWith( "[" ) ) {
			return buildArrayClassDetails( componentTypeName, modelsContext );
		}

		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		if ( componentTypeName.length() == 1 ) {
			// this is a primitive array...
			final PrimitiveKind primitiveKind = PrimitiveKind.resolveFromTypeChar( componentTypeName.charAt( 0 ) );
			return classDetailsRegistry.resolveClassDetails( primitiveKind.getTypeName() );
		}
		else {
			if ( !componentTypeName.startsWith( "L" ) ) {
				throw new AssertionError( "Unexpected array component type prefix - " + componentTypeName );
			}
			if ( !componentTypeName.endsWith( ";" ) ) {
				throw new AssertionError( "Unexpected array component type format : no semi-colon - " + componentTypeName );
			}
			final String objectComponentTypeName = componentTypeName.substring( 1, componentTypeName.length() - 1 );
			return classDetailsRegistry.resolveClassDetails( objectComponentTypeName.replace( '/', '.' ) );
		}
	}

	public static JdkClassDetails buildClassDetailsStatic(Class<?> javaClass, ModelsContext modelsContext) {
		return new JdkClassDetails( javaClass, modelsContext );
	}

	public static JdkMethodDetails buildMethodDetails(
			Method method,
			ClassDetails declaringType,
			ModelsContext modelsContext) {
		if ( method.getParameterCount() == 0 ) {
			// could be a getter
			final Class<?> returnType = method.getReturnType();
			if ( !isVoid( returnType )
					&& !ModifierUtils.isStatic( method.getModifiers() ) ) {
				final String methodName = method.getName();
				if ( methodName.startsWith( "get" ) ) {
					return buildGetterDetails( method, declaringType, modelsContext );
				}
				else if ( isBoolean( returnType ) && methodName.startsWith( "is" ) ) {
					return buildGetterDetails( method, declaringType, modelsContext );
				}
			}
		}

		if ( method.getParameterCount() == 1
				&& isVoid( method.getReturnType() )
				&& !ModifierUtils.isStatic( method.getModifiers() )
				&& method.getName().startsWith( "set" ) ) {
			return buildSetterDetails( method, declaringType, modelsContext );
		}

		return new JdkMethodDetails( method, MethodDetails.MethodKind.OTHER, null, declaringType, modelsContext );
	}

	public static JdkMethodDetails buildGetterDetails(
			Method method,
			ClassDetails declaringType,
			ModelsContext modelsContext) {
		return new JdkMethodDetails(
				method,
				MethodDetails.MethodKind.GETTER,
				toTypeDetails( method.getGenericReturnType(), modelsContext ),
				declaringType,
				modelsContext
		);
	}

	private static TypeDetails toTypeDetails(Type genericType, ModelsContext modelsContext) {
		return new JdkTrackingTypeSwitcher( modelsContext ).switchType( genericType );
	}

	public static JdkMethodDetails buildSetterDetails(
			Method method,
			ClassDetails declaringType,
			ModelsContext modelsContext) {
		return new JdkMethodDetails(
				method,
				MethodDetails.MethodKind.SETTER,
				toTypeDetails( method.getGenericParameterTypes()[0], modelsContext ),
				declaringType,
				modelsContext
		);
	}

	public static boolean isBoolean(Class<?> type) {
		return type == boolean.class || type == Boolean.class;
	}

	public static boolean isVoid(Class<?> type) {
		return type == void.class || type == Void.class;
	}
}
