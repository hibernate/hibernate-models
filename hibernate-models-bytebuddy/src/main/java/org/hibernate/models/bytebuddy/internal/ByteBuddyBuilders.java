/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

import org.hibernate.models.bytebuddy.internal.values.ArrayValueConverter;
import org.hibernate.models.bytebuddy.internal.values.ArrayValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.BooleanValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.ByteValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.CharacterValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.ClassValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.DoubleValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.EnumValueConverter;
import org.hibernate.models.bytebuddy.internal.values.EnumValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.FloatValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.IntegerValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.LongValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.NestedValueConverter;
import org.hibernate.models.bytebuddy.internal.values.NestedValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.ShortValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.StringValueExtractor;
import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.bytebuddy.spi.ValueExtractor;
import org.hibernate.models.internal.ArrayTypeDescriptor;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.util.CollectionHelper;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;

import static org.hibernate.models.internal.util.PrimitiveTypeHelper.resolvePrimitiveClass;

/**
 * @author Steve Ebersole
 */
public class ByteBuddyBuilders {
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Models - ClassDetails, MemberDetails
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Build a ClassDetails by name
	 *
	 * @param name The class name
	 * @param modelsContext The hibernate-models context
	 *
	 * @return The created ClassDetails; may be {@code null}.
	 */
	public static ClassDetails buildDetails(String name, ByteBuddyModelsContext modelsContext) {
		if ( StringHelper.isEmpty( name ) ) {
			return null;
		}

		if ( name.startsWith( "[" ) ) {
			// always handle arrays via the JDK builder
			return null;
		}

		if ( "void".equals( name ) ) {
			name = Void.class.getName();
		}

		// potentially handle primitives
		final Class<?> primitiveClass = resolvePrimitiveClass( name );
		if ( primitiveClass != null ) {
			return JdkBuilders.buildClassDetailsStatic( primitiveClass, modelsContext );
		}

		final TypePool typePool = modelsContext.getTypePool();

		try {
			return new ClassDetailsImpl( typePool.describe( name ).resolve(), modelsContext );
		}
		catch (Exception noClass) {
			// continue to the next checks
		}

		try {
			// potentially handle package names
			final String packageInfoName = name + ".package-info";
			// just make sure it is resolvable
			typePool.describe( packageInfoName ).resolve();
			// package-info is safe to load through using Class
			return JdkBuilders.buildClassDetailsStatic( packageInfoName, modelsContext );
		}
		catch (Exception noClass) {
			// continue to the next checks
		}

		return null;
	}

	/**
	 * Build a MethodDetails from the Byte Buddy form.
	 *
	 * @param method The Byte Buddy method descriptor
	 * @param declaringType ClassDetails for the declaring type
	 * @param modelsContext The hibernate-models context
	 *
	 * @return The MethodDetails
	 */
	public static MethodDetails buildMethodDetails(
			MethodDescription.InDefinedShape method,
			ClassDetailsImpl declaringType,
			ByteBuddyModelsContext modelsContext) {
		if ( method.getParameters().isEmpty() ) {
			// could be a getter
			final TypeDescription.Generic returnType = method.getReturnType();
			if ( !isVoid( returnType ) ) {
				final String methodName = method.getName();
				if ( methodName.startsWith( "get" ) ) {
					return new MethodDetailsImpl(
							method,
							MethodDetails.MethodKind.GETTER,
							TypeSwitchStandard.switchType( returnType, declaringType, modelsContext ),
							declaringType,
							modelsContext
					);
				}
				else if ( isBoolean( returnType ) && ( methodName.startsWith( "is" )
						|| methodName.startsWith( "has" )
						|| methodName.startsWith( "was" ) ) ) {
					return new MethodDetailsImpl(
							method,
							MethodDetails.MethodKind.GETTER,
							TypeSwitchStandard.switchType( returnType, declaringType, modelsContext ),
							declaringType,
							modelsContext
					);
				}
			}
		}

		if ( method.getParameters().size() == 1
				&& isVoid( method.getReturnType() )
				&& method.getName().startsWith( "set" ) ) {
			return new MethodDetailsImpl(
					method,
					MethodDetails.MethodKind.SETTER,
					TypeSwitchStandard.switchType( method.getParameters().get( 0 ).getType(), declaringType, modelsContext ),
					declaringType,
					modelsContext
			);
		}

		return new MethodDetailsImpl(
				method,
				MethodDetails.MethodKind.OTHER,
				null,
				declaringType,
				modelsContext
		);
	}

	public static boolean isVoid(TypeDescription.Generic type) {
		return type.represents( Void.class ) || type.represents( void.class );
	}

	public static boolean isBoolean(TypeDescription.Generic type) {
		return type.represents( Boolean.class ) || type.represents( boolean.class );
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// Annotation usage
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	/**
	 * Processes the annotations associated with an {@linkplain AnnotationSource},
	 * building and collecting an annotation usage for each and returns the collected
	 * usages.
	 *
	 * @param annotationSource The source of annotations
	 * @param modelsContext The hibernate-models context
	 *
	 * @return The collected usages
	 *
	 * @see #processAnnotations
	 */
	public static Map<Class<? extends Annotation>, ? extends Annotation> collectUsages(
			AnnotationSource annotationSource,
			ByteBuddyModelsContext modelsContext) {
		if ( annotationSource == null ) {
			return Collections.emptyMap();
		}
		final Map<Class<? extends Annotation>, Annotation> result = new HashMap<>();
		processAnnotations(
				annotationSource.getDeclaredAnnotations(),
				result::put,
				modelsContext
		);
		return result;
	}

	/**
	 * Process annotations, creating usage instances passed back to the consumer
	 *
	 * @param annotations List of annotations (in Byte Buddy form)
	 * @param consumer The consumer of created usage instances
	 * @param modelsContext The hibernate-models context
	 *
	 * @see #makeUsage
	 */
	public static void processAnnotations(
			AnnotationList annotations,
			BiConsumer<Class<? extends Annotation>, Annotation> consumer,
			ByteBuddyModelsContext modelsContext) {
		final AnnotationDescriptorRegistry annotationDescriptorRegistry = modelsContext.getAnnotationDescriptorRegistry();

		for ( AnnotationDescription annotation : annotations ) {
			if ( annotation.getAnnotationType().represents( Documented.class )
					|| annotation.getAnnotationType().represents( Repeatable.class )
					|| annotation.getAnnotationType().represents( Retention.class )
					|| annotation.getAnnotationType().represents( Target.class ) ) {
				continue;
			}

			final Class<? extends Annotation> annotationType = modelsContext
					.getClassLoading()
					.classForName( annotation.getAnnotationType().getTypeName() );
			final AnnotationDescriptor<?> annotationDescriptor = annotationDescriptorRegistry.getDescriptor( annotationType );
			final Annotation usage = makeUsage(
					annotation,
					annotationDescriptor,
					modelsContext
			);
			consumer.accept( annotationType, usage );
		}
	}

	/**
	 * Creates an annotation usage given the
	 * {@linkplain AnnotationDescription Byte Buddy form}.
	 *
	 * @param annotationDescription The Byte Buddy form
	 * @param annotationDescriptor The annotation (class) descriptor
	 * @param modelsContext The hibernate-models context
	 *
	 * @return The created annotation usage.
	 */
	public static <A extends Annotation> A makeUsage(
			AnnotationDescription annotationDescription,
			AnnotationDescriptor<A> annotationDescriptor,
			ModelsContext modelsContext) {
		final Map<String, Object> attributeValues = extractAttributeValues(
				annotationDescription,
				annotationDescriptor,
				modelsContext
		);
		return annotationDescriptor.createUsage( attributeValues, modelsContext );
	}

	private static <A extends Annotation> Map<String, Object> extractAttributeValues(
			AnnotationDescription annotationDescription,
			AnnotationDescriptor<A> annotationDescriptor,
			ModelsContext modelContext) {

		if ( CollectionHelper.isEmpty( annotationDescriptor.getAttributes() ) ) {
			return Collections.emptyMap();
		}

		final ConcurrentHashMap<String, Object> valueMap = new ConcurrentHashMap<>();
		for ( int i = 0; i < annotationDescriptor.getAttributes().size(); i++ ) {
			final AttributeDescriptor<?> attributeDescriptor = annotationDescriptor.getAttributes().get( i );
			final ValueExtractor<?> extractor = modelContext
					.as( ByteBuddyModelsContext.class )
					.getValueExtractor( attributeDescriptor.getTypeDescriptor() );
			final Object attributeValue = extractor.extractValue(
					annotationDescription,
					attributeDescriptor.getName(),
					modelContext
			);
			valueMap.put( attributeDescriptor.getName(), attributeValue );
		}
		return valueMap;
	}


	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	// ValueExtractor
	// ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

	@SuppressWarnings("unchecked")
	public static <V> ValueExtractor<V> buildValueExtractor(
			ValueTypeDescriptor<V> valueTypeDescriptor,
			ByteBuddyModelsContextImpl modelsContext) {
		if ( valueTypeDescriptor.getValueType().isArray() ) {
			final ValueTypeDescriptor<?> elementTypeDescriptor = ( (ArrayTypeDescriptor<?>) valueTypeDescriptor ).getElementTypeDescriptor();
			final ArrayValueConverter<?> valueConverter = new ArrayValueConverter<>( elementTypeDescriptor );
			final ArrayValueExtractor<?> valueExtractor = new ArrayValueExtractor<>( valueConverter );
			return (ValueExtractor<V>) valueExtractor;
		}

		if ( isBoolean( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) BooleanValueExtractor.BOOLEAN_EXTRACTOR;
		}

		if ( isByte( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) ByteValueExtractor.BYTE_EXTRACTOR;
		}

		if ( isChar( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) CharacterValueExtractor.CHARACTER_EXTRACTOR;
		}

		if ( isDouble( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) DoubleValueExtractor.DOUBLE_EXTRACTOR;
		}

		if ( isFloat( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) FloatValueExtractor.FLOAT_EXTRACTOR;
		}

		if ( isInt( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) IntegerValueExtractor.INTEGER_EXTRACTOR;
		}

		if ( isLong( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) LongValueExtractor.LONG_EXTRACTOR;
		}

		if ( isShort( valueTypeDescriptor ) ) {
			return (ValueExtractor<V>) ShortValueExtractor.SHORT_EXTRACTOR;
		}

		if ( valueTypeDescriptor.getValueType() == String.class ) {
			return (ValueExtractor<V>) StringValueExtractor.STRING_EXTRACTOR;
		}

		if ( valueTypeDescriptor.getValueType().isAnnotation() ) {
			final AnnotationDescriptor<? extends Annotation> annotationDescriptor = modelsContext
					.getAnnotationDescriptorRegistry()
					.getDescriptor( (Class<? extends Annotation>) valueTypeDescriptor.getValueType() );
			final NestedValueConverter<? extends Annotation> jandexNestedValueConverter = new NestedValueConverter<>( annotationDescriptor );
			final NestedValueExtractor<? extends Annotation> jandexNestedValueExtractor = new NestedValueExtractor<>( jandexNestedValueConverter );
			return (ValueExtractor<V>) jandexNestedValueExtractor;
		}

		if ( valueTypeDescriptor.getValueType().isEnum() ) {
			//noinspection rawtypes
			final EnumValueConverter<? extends Enum> converter = new EnumValueConverter( valueTypeDescriptor.getValueType() );
			//noinspection rawtypes
			final EnumValueExtractor extractor = new EnumValueExtractor<>( converter );
			return (ValueExtractor<V>) extractor;
		}

		if ( valueTypeDescriptor.getValueType() == Class.class ) {
			return (ValueExtractor<V>) ClassValueExtractor.CLASS_EXTRACTOR;
		}

		throw new UnsupportedOperationException( "Unhandled value type : " + valueTypeDescriptor );
	}

	private static <V> boolean isBoolean(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == boolean.class
				|| valueTypeDescriptor.getValueType() == Boolean.class;
	}

	private static <V> boolean isByte(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == byte.class
				|| valueTypeDescriptor.getValueType() == Byte.class;
	}

	private static <V> boolean isChar(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == char.class
				|| valueTypeDescriptor.getValueType() == Character.class;
	}

	private static <V> boolean isDouble(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == double.class
				|| valueTypeDescriptor.getValueType() == Double.class;
	}

	private static <V> boolean isFloat(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == float.class
				|| valueTypeDescriptor.getValueType() == Float.class;
	}

	private static <V> boolean isShort(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == short.class
				|| valueTypeDescriptor.getValueType() == Short.class;
	}

	private static <V> boolean isInt(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == int.class
				|| valueTypeDescriptor.getValueType() == Integer.class;
	}

	private static <V> boolean isLong(ValueTypeDescriptor<V> valueTypeDescriptor) {
		return valueTypeDescriptor.getValueType() == long.class
				|| valueTypeDescriptor.getValueType() == Long.class;
	}
}
