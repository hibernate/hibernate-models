/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.internal;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

import org.hibernate.models.bytebuddy.internal.values.ArrayValueConverter;
import org.hibernate.models.bytebuddy.internal.values.ArrayValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.BooleanValueConverter;
import org.hibernate.models.bytebuddy.internal.values.BooleanValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.ByteValueConverter;
import org.hibernate.models.bytebuddy.internal.values.ByteValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.CharacterValueConverter;
import org.hibernate.models.bytebuddy.internal.values.CharacterValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.ClassValueConverter;
import org.hibernate.models.bytebuddy.internal.values.ClassValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.DoubleValueConverter;
import org.hibernate.models.bytebuddy.internal.values.DoubleValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.EnumValueConverter;
import org.hibernate.models.bytebuddy.internal.values.EnumValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.FloatValueConverter;
import org.hibernate.models.bytebuddy.internal.values.FloatValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.IntegerValueConverter;
import org.hibernate.models.bytebuddy.internal.values.IntegerValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.LongValueConverter;
import org.hibernate.models.bytebuddy.internal.values.LongValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.NestedValueConverter;
import org.hibernate.models.bytebuddy.internal.values.NestedValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.ShortValueConverter;
import org.hibernate.models.bytebuddy.internal.values.ShortValueExtractor;
import org.hibernate.models.bytebuddy.internal.values.StringValueConverter;
import org.hibernate.models.bytebuddy.internal.values.StringValueExtractor;
import org.hibernate.models.bytebuddy.spi.ByteBuddyModelsContext;
import org.hibernate.models.bytebuddy.spi.ValueConverter;
import org.hibernate.models.bytebuddy.spi.ValueExtractor;
import org.hibernate.models.internal.ArrayTypeDescriptor;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ValueTypeDescriptor;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.pool.TypePool;

/**
 * @author Steve Ebersole
 */
public class ModelBuilders {
	public static ClassDetails buildDetails(String name, ByteBuddyModelsContext modelContext) {
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
			return JdkBuilders.buildClassDetailsStatic( primitiveClass, modelContext );
		}

		final TypePool typePool = modelContext.getTypePool();

		try {
			return new ClassDetailsImpl( typePool.describe( name ).resolve(), modelContext );
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
			return JdkBuilders.buildClassDetailsStatic( packageInfoName, modelContext );
		}
		catch (Exception noClass) {
			// continue to the next checks
		}

		return null;
	}


	public static Class<?> resolvePrimitiveClass(String className) {
		if ( "boolean".equals( className ) ) {
			return boolean.class;
		}

		if ( Boolean.class.getSimpleName().equalsIgnoreCase( className ) || Boolean.class.getName().equals( className ) ) {
			return Boolean.class;
		}

		if ( "byte".equals( className ) ) {
			return byte.class;
		}

		if ( Byte.class.getSimpleName().equals( className ) || Byte.class.getName().equals( className ) ) {
			return Byte.class;
		}

		if ( "short".equals( className ) ) {
			return short.class;
		}

		if ( Short.class.getSimpleName().equals( className ) || Short.class.getName().equals( className ) ) {
			return Short.class;
		}

		if ( "int".equals( className ) ) {
			return int.class;
		}

		if ( Integer.class.getSimpleName().equals( className ) || Integer.class.getName().equals( className ) ) {
			return Integer.class;
		}

		if ( "long".equals( className ) ) {
			return long.class;
		}

		if ( Long.class.getSimpleName().equals( className ) || Long.class.getName().equals( className ) ) {
			return Long.class;
		}

		if ( "double".equals( className ) ) {
			return double.class;
		}

		if ( Double.class.getSimpleName().equals( className ) || Double.class.getName().equals( className ) ) {
			return Double.class;
		}

		if ( "float".equals( className ) ) {
			return float.class;
		}

		if ( Float.class.getSimpleName().equals( className ) || Float.class.getName().equals( className ) ) {
			return Float.class;
		}

		if ( "char".equals( className ) ) {
			return char.class;
		}

		if ( Character.class.getSimpleName().equals( className ) || Character.class.getName().equals( className ) ) {
			return Character.class;
		}

		return null;
	}

	public static MethodDetails buildMethodDetails(
			MethodDescription.InDefinedShape method,
			ClassDetailsImpl declaringType,
			ByteBuddyModelsContext modelContext) {
		if ( method.getParameters().isEmpty() ) {
			// could be a getter
			final TypeDescription.Generic returnType = method.getReturnType();
			if ( !isVoid( returnType ) ) {
				final String methodName = method.getName();
				if ( methodName.startsWith( "get" ) ) {
					return new MethodDetailsImpl(
							method,
							MethodDetails.MethodKind.GETTER,
							TypeSwitchStandard.switchType( returnType, declaringType, modelContext ),
							declaringType,
							modelContext
					);
				}
				else if ( isBoolean( returnType ) && ( methodName.startsWith( "is" )
						|| methodName.startsWith( "has" )
						|| methodName.startsWith( "was" ) ) ) {
					return new MethodDetailsImpl(
							method,
							MethodDetails.MethodKind.GETTER,
							TypeSwitchStandard.switchType( returnType, declaringType, modelContext ),
							declaringType,
							modelContext
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
					TypeSwitchStandard.switchType( method.getParameters().get( 0 ).getType(), declaringType, modelContext ),
					declaringType,
					modelContext
			);
		}

		return new MethodDetailsImpl(
				method,
				MethodDetails.MethodKind.OTHER,
				null,
				declaringType,
				modelContext
		);
	}

	public static boolean isVoid(TypeDescription.Generic type) {
		return type.represents( Void.class ) || type.represents( void.class );
	}

	public static boolean isBoolean(TypeDescription.Generic type) {
		return type.represents( Boolean.class ) || type.represents( boolean.class );
	}

	@SuppressWarnings("unchecked")
	public static <V> ValueConverter<V> buildValueHandlersReturnConverter(
			ValueTypeDescriptor<V> valueTypeDescriptor,
			BiConsumer<ValueTypeDescriptor<V>,ValueConverter<V>> converterCollector,
			BiConsumer<ValueTypeDescriptor<V>, ValueExtractor<V>> extractorCollector,
			ByteBuddyModelContextImpl sourceModelBuildingContext) {
		if ( valueTypeDescriptor.getValueType().isArray() ) {
			final ValueTypeDescriptor<?> elementTypeDescriptor = ( (ArrayTypeDescriptor<?>) valueTypeDescriptor ).getElementTypeDescriptor();
			final ArrayValueConverter<?> valueConverter = new ArrayValueConverter<>( elementTypeDescriptor );
			final ArrayValueExtractor<?> valueExtractor = new ArrayValueExtractor<>( valueConverter );
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) valueConverter );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) valueExtractor );
			return (ValueConverter<V>) valueConverter;
		}

		if ( isBoolean( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) BooleanValueConverter.BOOLEAN_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) BooleanValueExtractor.BOOLEAN_EXTRACTOR );
			return (ValueConverter<V>) BooleanValueConverter.BOOLEAN_VALUE_WRAPPER;
		}

		if ( isByte( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) ByteValueConverter.BYTE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) ByteValueExtractor.BYTE_EXTRACTOR );
			return (ValueConverter<V>) ByteValueConverter.BYTE_VALUE_WRAPPER;
		}

		if ( isChar( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) CharacterValueConverter.CHARACTER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) CharacterValueExtractor.CHARACTER_EXTRACTOR );
			return (ValueConverter<V>) CharacterValueConverter.CHARACTER_VALUE_WRAPPER;
		}

		if ( isDouble( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) DoubleValueConverter.DOUBLE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) DoubleValueExtractor.DOUBLE_EXTRACTOR );
			return (ValueConverter<V>) DoubleValueConverter.DOUBLE_VALUE_WRAPPER;
		}

		if ( isFloat( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) FloatValueConverter.FLOAT_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) FloatValueExtractor.FLOAT_EXTRACTOR );
			return (ValueConverter<V>) FloatValueConverter.FLOAT_VALUE_WRAPPER;
		}

		if ( isInt( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) IntegerValueConverter.INTEGER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) IntegerValueExtractor.INTEGER_EXTRACTOR );
			return (ValueConverter<V>) IntegerValueConverter.INTEGER_VALUE_WRAPPER;
		}

		if ( isLong( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) LongValueConverter.LONG_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) LongValueExtractor.LONG_EXTRACTOR );
			return (ValueConverter<V>) LongValueConverter.LONG_VALUE_WRAPPER;
		}

		if ( isShort( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) ShortValueConverter.SHORT_VALUE_WRAPPER  );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) ShortValueExtractor.SHORT_EXTRACTOR );
			return (ValueConverter<V>) ShortValueConverter.SHORT_VALUE_WRAPPER;
		}

		if ( valueTypeDescriptor.getValueType() == String.class ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) StringValueConverter.STRING_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) StringValueExtractor.STRING_EXTRACTOR );
			return (ValueConverter<V>) StringValueConverter.STRING_VALUE_WRAPPER;
		}

		if ( valueTypeDescriptor.getValueType().isAnnotation() ) {
			final AnnotationDescriptor<? extends Annotation> annotationDescriptor = sourceModelBuildingContext
					.getAnnotationDescriptorRegistry()
					.getDescriptor( (Class<? extends Annotation>) valueTypeDescriptor.getValueType() );
			final NestedValueConverter<? extends Annotation> jandexNestedValueConverter = new NestedValueConverter<>( annotationDescriptor );
			final NestedValueExtractor<? extends Annotation> jandexNestedValueExtractor = new NestedValueExtractor<>( jandexNestedValueConverter );

			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) jandexNestedValueConverter );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) jandexNestedValueExtractor );
			return (ValueConverter<V>) jandexNestedValueConverter;
		}

		if ( valueTypeDescriptor.getValueType().isEnum() ) {
			//noinspection rawtypes
			final EnumValueConverter<? extends Enum> converter = new EnumValueConverter( valueTypeDescriptor.getValueType() );
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) converter );
			//noinspection rawtypes
			extractorCollector.accept( valueTypeDescriptor, new EnumValueExtractor( converter ) );
			return (ValueConverter<V>) converter;
		}

		if ( valueTypeDescriptor.getValueType() == Class.class ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) ClassValueConverter.CLASS_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) ClassValueExtractor.CLASS_EXTRACTOR );
			return (ValueConverter<V>) ClassValueConverter.CLASS_VALUE_WRAPPER;
		}

		throw new UnsupportedOperationException( "Unhandled value type : " + valueTypeDescriptor );
	}

	@SuppressWarnings("unchecked")
	public static <V> ValueExtractor<V> buildValueHandlersReturnExtractor(
			ValueTypeDescriptor<V> valueTypeDescriptor,
			BiConsumer<ValueTypeDescriptor<V>,ValueConverter<V>> converterCollector,
			BiConsumer<ValueTypeDescriptor<V>, ValueExtractor<V>> extractorCollector,
			ByteBuddyModelContextImpl sourceModelBuildingContext) {
		if ( valueTypeDescriptor.getValueType().isArray() ) {
			final ValueTypeDescriptor<?> elementTypeDescriptor = ( (ArrayTypeDescriptor<?>) valueTypeDescriptor ).getElementTypeDescriptor();
			final ArrayValueConverter<?> valueConverter = new ArrayValueConverter<>( elementTypeDescriptor );
			final ArrayValueExtractor<?> valueExtractor = new ArrayValueExtractor<>( valueConverter );
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) valueConverter );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) valueExtractor );
			return (ValueExtractor<V>) valueExtractor;
		}

		if ( isBoolean( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) BooleanValueConverter.BOOLEAN_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) BooleanValueExtractor.BOOLEAN_EXTRACTOR );
			return (ValueExtractor<V>) BooleanValueExtractor.BOOLEAN_EXTRACTOR;
		}

		if ( isByte( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) ByteValueConverter.BYTE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) ByteValueExtractor.BYTE_EXTRACTOR );
			return (ValueExtractor<V>) ByteValueExtractor.BYTE_EXTRACTOR;
		}

		if ( isChar( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) CharacterValueConverter.CHARACTER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) CharacterValueExtractor.CHARACTER_EXTRACTOR );
			return (ValueExtractor<V>) CharacterValueExtractor.CHARACTER_EXTRACTOR;
		}

		if ( isDouble( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) DoubleValueConverter.DOUBLE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) DoubleValueExtractor.DOUBLE_EXTRACTOR );
			return (ValueExtractor<V>) DoubleValueExtractor.DOUBLE_EXTRACTOR;
		}

		if ( isFloat( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) FloatValueConverter.FLOAT_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) FloatValueExtractor.FLOAT_EXTRACTOR );
			return (ValueExtractor<V>) FloatValueExtractor.FLOAT_EXTRACTOR;
		}

		if ( isInt( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) IntegerValueConverter.INTEGER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) IntegerValueExtractor.INTEGER_EXTRACTOR );
			return (ValueExtractor<V>) IntegerValueExtractor.INTEGER_EXTRACTOR;
		}

		if ( isLong( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) LongValueConverter.LONG_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) LongValueExtractor.LONG_EXTRACTOR );
			return (ValueExtractor<V>) LongValueExtractor.LONG_EXTRACTOR;
		}

		if ( isShort( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) ShortValueConverter.SHORT_VALUE_WRAPPER  );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) ShortValueExtractor.SHORT_EXTRACTOR );
			return (ValueExtractor<V>) ShortValueExtractor.SHORT_EXTRACTOR;
		}

		if ( valueTypeDescriptor.getValueType() == String.class ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) StringValueConverter.STRING_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) StringValueExtractor.STRING_EXTRACTOR );
			return (ValueExtractor<V>) StringValueExtractor.STRING_EXTRACTOR;
		}

		if ( valueTypeDescriptor.getValueType().isAnnotation() ) {
			final AnnotationDescriptor<? extends Annotation> annotationDescriptor = sourceModelBuildingContext
					.getAnnotationDescriptorRegistry()
					.getDescriptor( (Class<? extends Annotation>) valueTypeDescriptor.getValueType() );
			final NestedValueConverter<? extends Annotation> jandexNestedValueConverter = new NestedValueConverter<>( annotationDescriptor );
			final NestedValueExtractor<? extends Annotation> jandexNestedValueExtractor = new NestedValueExtractor<>( jandexNestedValueConverter );

			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) jandexNestedValueConverter );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) jandexNestedValueExtractor );
			return (ValueExtractor<V>) jandexNestedValueExtractor;
		}

		if ( valueTypeDescriptor.getValueType().isEnum() ) {
			//noinspection rawtypes
			final EnumValueConverter<? extends Enum> converter = new EnumValueConverter( valueTypeDescriptor.getValueType() );
			//noinspection rawtypes
			final EnumValueExtractor extractor = new EnumValueExtractor<>( converter );
			converterCollector.accept( valueTypeDescriptor, (ValueConverter<V>) converter );
			extractorCollector.accept( valueTypeDescriptor, extractor );
			return (ValueExtractor<V>) extractor;
		}

		if ( valueTypeDescriptor.getValueType() == Class.class ) {
			converterCollector.accept( valueTypeDescriptor,  (ValueConverter<V>) ClassValueConverter.CLASS_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (ValueExtractor<V>) ClassValueExtractor.CLASS_EXTRACTOR );
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
