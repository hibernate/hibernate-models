/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.internal.PrimitiveKind;
import org.hibernate.models.internal.StandardAnnotationDescriptor;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * ClassDetailsBuilder implementation based on {@link Class}
 *
 * @author Steve Ebersole
 */
public class JdkBuilders implements ClassDetailsBuilder {
	/**
	 * Singleton access
	 */
	public static final JdkBuilders DEFAULT_BUILDER = new JdkBuilders();

	@Override
	public JdkClassDetails buildClassDetails(String name, SourceModelBuildingContext buildingContext) {
		return buildClassDetailsStatic( name, buildingContext );
	}

	public static JdkClassDetails buildClassDetailsStatic(String name, SourceModelBuildingContext buildingContext) {
		if ( char.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( char.class, buildingContext );
		}
		if ( byte.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( byte.class, buildingContext );
		}
		if ( boolean.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( boolean.class, buildingContext );
		}
		if ( short.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( short.class, buildingContext );
		}
		if ( int.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( int.class, buildingContext );
		}
		if ( long.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( long.class, buildingContext );
		}
		if ( float.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( float.class, buildingContext );
		}
		if ( double.class.getName().equals( name ) ) {
			return buildClassDetailsStatic( double.class, buildingContext );
		}
		if ( name.startsWith( "[" ) ) {
			return buildArrayClassDetails( name, buildingContext );
		}

		try {
			final Class<Object> loadedClass = buildingContext.getClassLoading().classForName( name );
			return buildClassDetailsStatic( loadedClass, buildingContext );
		}
		catch (UnknownClassException e) {
			// see if it might be a package name...
			try {
				final Class<Object> packageInfoClass = buildingContext.getClassLoading().classForName( name + ".package-info" );
				return buildClassDetailsStatic( packageInfoClass, buildingContext );
			}
			catch (UnknownClassException noPackage) {
				throw e;
			}
		}
	}

	private static JdkClassDetails buildArrayClassDetails(String name, SourceModelBuildingContext buildingContext) {
		assert name.startsWith( "[" );
		final int dimensionCount = StringHelper.countArrayDimensions( name );
		assert dimensionCount > 0;

		final String componentTypeName = name.substring( 1 );
		final ClassDetails componentTypeDetails = resolveArrayComponentType( componentTypeName, buildingContext );

		final Class<?> javaClass = componentTypeDetails.toJavaClass();
		final Class<?> arrayType = javaClass.arrayType();
		return new JdkClassDetails( arrayType, buildingContext );
	}

	private static ClassDetails resolveArrayComponentType(String componentTypeName, SourceModelBuildingContext buildingContext) {
		if ( componentTypeName.startsWith( "[" ) ) {
			return buildArrayClassDetails( componentTypeName, buildingContext );
		}

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
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

	public static JdkClassDetails buildClassDetailsStatic(Class<?> javaClass, SourceModelBuildingContext buildingContext) {
		return new JdkClassDetails( javaClass, buildingContext );
	}

	public static JdkMethodDetails buildMethodDetails(
			Method method,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		if ( method.getParameterCount() == 0 ) {
			// could be a getter
			final Class<?> returnType = method.getReturnType();
			if ( !isVoid( returnType )
					&& !ModifierUtils.isStatic( method.getModifiers() ) ) {
				final String methodName = method.getName();
				if ( methodName.startsWith( "get" ) ) {
					return buildGetterDetails( method, declaringType, buildingContext );
				}
				else if ( isBoolean( returnType ) && methodName.startsWith( "is" ) ) {
					return buildGetterDetails( method, declaringType, buildingContext );
				}
			}
		}

		if ( method.getParameterCount() == 1
				&& isVoid( method.getReturnType() )
				&& !ModifierUtils.isStatic( method.getModifiers() )
				&& method.getName().startsWith( "set" ) ) {
			return buildSetterDetails( method, declaringType, buildingContext );
		}

		return new JdkMethodDetails( method, MethodDetails.MethodKind.OTHER, null, declaringType, buildingContext );
	}

	public static JdkMethodDetails buildGetterDetails(
			Method method,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		return new JdkMethodDetails(
				method,
				MethodDetails.MethodKind.GETTER,
				toTypeDetails( method.getGenericReturnType(), buildingContext ),
				declaringType,
				buildingContext
		);
	}

	private static TypeDetails toTypeDetails(Type genericType, SourceModelBuildingContext buildingContext) {
		return new JdkTrackingTypeSwitcher( buildingContext ).switchType( genericType );
	}

	public static JdkMethodDetails buildSetterDetails(
			Method method,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		return new JdkMethodDetails(
				method,
				MethodDetails.MethodKind.SETTER,
				toTypeDetails( method.getGenericParameterTypes()[0], buildingContext ),
				declaringType,
				buildingContext
		);
	}

	public static boolean isBoolean(Class<?> type) {
		return type == boolean.class || type == Boolean.class;
	}

	public static boolean isVoid(Class<?> type) {
		return type == void.class || type == Void.class;
	}

	public static <A extends Annotation> AnnotationDescriptor<A> buildAnnotationDescriptor(
			Class<A> annotationType,
			SourceModelBuildingContext modelContext) {
		return buildAnnotationDescriptor(
				annotationType,
				resolveRepeatableContainerDescriptor( annotationType, modelContext ),
				modelContext
		);
	}

	public static <A extends Annotation, C extends Annotation> AnnotationDescriptor<C> resolveRepeatableContainerDescriptor(
			Class<A> annotationType,
			SourceModelBuildingContext modelContext) {
		final Repeatable repeatableAnnotation = annotationType.getAnnotation( Repeatable.class );
		if ( repeatableAnnotation == null ) {
			return null;
		}
		final AnnotationDescriptorRegistry descriptorRegistry = modelContext.getAnnotationDescriptorRegistry();
		//noinspection unchecked
		final AnnotationDescriptor<C> containerDescriptor = (AnnotationDescriptor<C>) descriptorRegistry.getDescriptor( repeatableAnnotation.value() );
		( (AnnotationDescriptorRegistryStandard) descriptorRegistry ).register( containerDescriptor );
		return containerDescriptor;
	}

	public static <A extends Annotation> AnnotationDescriptor<A> buildAnnotationDescriptor(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer,
			SourceModelBuildingContext modelContext) {
		return new StandardAnnotationDescriptor<>( annotationType, repeatableContainer, modelContext );
	}

}
