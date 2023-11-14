/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal.jdk;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.internal.AnnotationDescriptorRegistryStandard;
import org.hibernate.models.internal.ModifierUtils;
import org.hibernate.models.internal.TypeDescriptors;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

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
		return buildClassDetailsStatic(
				buildingContext.getClassLoading().classForName( name ),
				buildingContext
		);
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
			switch ( componentTypeName ) {
				case "Z" -> {
					return classDetailsRegistry.resolveClassDetails( "boolean" );
				}
				case "B" -> {
					return classDetailsRegistry.resolveClassDetails( "byte" );
				}
				case "C" -> {
					return classDetailsRegistry.resolveClassDetails( "char" );
				}
				case "S" -> {
					return classDetailsRegistry.resolveClassDetails( "short" );
				}
				case "I" -> {
					return classDetailsRegistry.resolveClassDetails( "int" );
				}
				case "J" -> {
					return classDetailsRegistry.resolveClassDetails( "long" );
				}
				case "D" -> {
					return classDetailsRegistry.resolveClassDetails( "double" );
				}
				case "F" -> {
					return classDetailsRegistry.resolveClassDetails( "float" );
				}
			}
		}
		else {
			assert componentTypeName.startsWith( "L" );
			assert componentTypeName.endsWith( ";" );
			final String objectComponentTypeName = componentTypeName.substring( 1, componentTypeName.length() - 1 );
			return classDetailsRegistry.resolveClassDetails( objectComponentTypeName );
		}

		throw new UnsupportedOperationException( "Support for non-primitive arrays not yet implemented" );
	}

	public static JdkClassDetails buildClassDetailsStatic(Class<?> javaClass, SourceModelBuildingContext buildingContext) {
		return new JdkClassDetails( javaClass, buildingContext );
	}

	public static JdkMethodDetails buildMethodDetails(Method method, SourceModelBuildingContext buildingContext) {
		if ( method.getParameterCount() == 0 ) {
			// could be a getter
			final Class<?> returnType = method.getReturnType();
			if ( !isVoid( returnType )
					&& !ModifierUtils.isStatic( method.getModifiers() ) ) {
				final String methodName = method.getName();
				if ( methodName.startsWith( "get" ) ) {
					return buildGetterDetails( method, returnType, buildingContext );
				}
				else if ( isBoolean( returnType ) && methodName.startsWith( "is" ) ) {
					return buildGetterDetails( method, returnType, buildingContext );
				}
			}
		}

		if ( method.getParameterCount() == 1
				&& isVoid( method.getReturnType() )
				&& !ModifierUtils.isStatic( method.getModifiers() )
				&& method.getName().startsWith( "set" ) ) {
			return buildSetterDetails( method, method.getParameterTypes()[0], buildingContext );
		}

		return new JdkMethodDetails( method, MethodDetails.MethodKind.OTHER, null, buildingContext );
	}

	public static JdkMethodDetails buildGetterDetails(
			Method method,
			Class<?> type,
			SourceModelBuildingContext buildingContext) {
		assert type != null;
		return new JdkMethodDetails(
				method,
				MethodDetails.MethodKind.GETTER,
				buildingContext.getClassDetailsRegistry().resolveClassDetails( type.getName() ),
				buildingContext
		);
	}

	public static JdkMethodDetails buildSetterDetails(
			Method method,
			Class<?> type,
			SourceModelBuildingContext buildingContext) {
		assert type != null;
		return new JdkMethodDetails(
				method,
				MethodDetails.MethodKind.SETTER,
				buildingContext.getClassDetailsRegistry().resolveClassDetails( type.getName() ),
				buildingContext
		);
	}

	private static boolean isBoolean(Class<?> type) {
		return type == boolean.class || type == Boolean.class;
	}

	private static boolean isVoid(Class<?> type) {
		return type == void.class || type == Void.class;
	}

	public static <A extends Annotation> AnnotationDescriptorOrmImpl<A> buildAnnotationDescriptor(
			Class<A> annotationType,
			AnnotationDescriptorRegistry descriptorRegistry) {
		return buildAnnotationDescriptor(
				annotationType,
				resolveRepeatableContainerDescriptor( annotationType, descriptorRegistry )
		);
	}

	public static <A extends Annotation, C extends Annotation> AnnotationDescriptor<C> resolveRepeatableContainerDescriptor(
			Class<A> annotationType,
			AnnotationDescriptorRegistry descriptorRegistry) {
		final Repeatable repeatableAnnotation = annotationType.getAnnotation( Repeatable.class );
		if ( repeatableAnnotation == null ) {
			return null;
		}
		//noinspection unchecked
		final AnnotationDescriptor<C> containerDescriptor = (AnnotationDescriptor<C>) descriptorRegistry.getDescriptor( repeatableAnnotation.value() );
		( (AnnotationDescriptorRegistryStandard) descriptorRegistry ).register( containerDescriptor );
		return containerDescriptor;
	}

	public static <A extends Annotation> AnnotationDescriptorOrmImpl<A> buildAnnotationDescriptor(
			Class<A> annotationType,
			AnnotationDescriptor<?> repeatableContainer) {
		return new AnnotationDescriptorOrmImpl<>( annotationType, repeatableContainer );
	}

	public static <A extends Annotation> List<AttributeDescriptor<?>> extractAttributeDescriptors(
			AnnotationDescriptor<A> annotationDescriptor,
			Class<A> annotationType) {
		final Method[] methods = annotationType.getDeclaredMethods();
		final List<AttributeDescriptor<?>> attributeDescriptors = new ArrayList<>( methods.length );
		for ( Method method : methods ) {
			attributeDescriptors.add( createAttributeDescriptor( annotationDescriptor, method ) );
		}
		return attributeDescriptors;
	}

	private static <X, A extends Annotation> AttributeDescriptor<X> createAttributeDescriptor(
			AnnotationDescriptor<A> annotationDescriptor,
			Method method) {
		//noinspection unchecked
		final Class<X> attributeType = (Class<X>) method.getReturnType();

		final ValueTypeDescriptor<X> typeDescriptor = TypeDescriptors.resolveTypeDescriptor( attributeType );
		return typeDescriptor.createAttributeDescriptor( annotationDescriptor, method.getName() );
	}
}
