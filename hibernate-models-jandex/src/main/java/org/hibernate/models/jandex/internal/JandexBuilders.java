/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.jandex.internal;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;

import org.hibernate.models.internal.ArrayTypeDescriptor;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.jandex.spi.JandexValueConverter;
import org.hibernate.models.jandex.spi.JandexValueExtractor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.ValueTypeDescriptor;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.IndexView;
import org.jboss.jandex.MethodInfo;
import org.jboss.jandex.Type;

/**
 * Jandex based ClassDetailsBuilder
 *
 * @author Steve Ebersole
 */
public class JandexBuilders {
	public static ClassDetails buildDetailsFromIndex(
			String name,
			IndexView jandexIndex,
			SourceModelBuildingContext processingContext) {
		if ( StringHelper.isEmpty( name ) ) {
			return null;
		}

		if ( "void".equals( name ) ) {
			name = Void.class.getName();
		}

		final ClassInfo classInfo = jandexIndex.getClassByName( name );
		if ( classInfo != null ) {
			return new JandexClassDetails( classInfo, processingContext );
		}

		// potentially handle primitives
		final Class<?> primitiveClass = resolvePrimitiveClass( name );
		if ( primitiveClass != null ) {
			return JdkBuilders.buildClassDetailsStatic( primitiveClass, processingContext );
		}

		// potentially handle package names
		final ClassInfo packageInfo = jandexIndex.getClassByName( name + ".package-info" );
		if ( packageInfo != null ) {
			// package-info is safe to load through using Class
			return JdkBuilders.buildClassDetailsStatic( name + ".package-info", processingContext );
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

		return null;
	}

	public static JandexMethodDetails buildMethodDetails(
			MethodInfo method,
			ClassDetails declaringType,
			SourceModelBuildingContext buildingContext) {
		if ( method.parametersCount() == 0 ) {
			// could be a getter
			final Type returnType = method.returnType();
			if ( returnType.kind() != Type.Kind.VOID ) {
				final String methodName = method.name();
				if ( methodName.startsWith( "get" ) ) {
					return new JandexMethodDetails(
							method,
							MethodDetails.MethodKind.GETTER,
							JandexTypeSwitchStandard.switchType( returnType, declaringType, buildingContext ),
							declaringType,
							buildingContext
					);
				}
				else if ( isBoolean( returnType ) && ( methodName.startsWith( "is" )
						|| methodName.startsWith( "has" )
						|| methodName.startsWith( "was" ) ) ) {
					return new JandexMethodDetails(
							method,
							MethodDetails.MethodKind.GETTER,
							JandexTypeSwitchStandard.switchType( returnType, declaringType, buildingContext ),
							declaringType,
							buildingContext
					);
				}
			}
		}

		if ( method.parametersCount() == 1
				&& method.returnType().kind() == Type.Kind.VOID
				&& method.name().startsWith( "set" ) ) {
			return new JandexMethodDetails(
					method,
					MethodDetails.MethodKind.SETTER,
					JandexTypeSwitchStandard.switchType( method.parameterType( 0 ), declaringType, buildingContext ),
					declaringType,
					buildingContext
			);
		}

		return new JandexMethodDetails(
				method,
				MethodDetails.MethodKind.OTHER,
				null,
				declaringType,
				buildingContext
		);
	}

	private static boolean isBoolean(Type type) {
		if ( type.kind() == Type.Kind.PRIMITIVE ) {
			return type.name().toString().equals( "boolean" );
		}
		return type.name().toString().equals( "java.lang.Boolean" );
	}

	@SuppressWarnings("unchecked")
	public static <V> JandexValueConverter<V> buildValueHandlersReturnConverter(
			ValueTypeDescriptor<V> valueTypeDescriptor,
			BiConsumer<ValueTypeDescriptor<V>,JandexValueConverter<V>> converterCollector,
			BiConsumer<ValueTypeDescriptor<V>, JandexValueExtractor<V>> extractorCollector,
			SourceModelBuildingContext buildingContext) {
		if ( valueTypeDescriptor.getValueType().isArray() ) {
			final ValueTypeDescriptor<?> elementTypeDescriptor = ( (ArrayTypeDescriptor<?>) valueTypeDescriptor ).getElementTypeDescriptor();
			final ArrayValueConverter<?> valueConverter = new ArrayValueConverter<>( elementTypeDescriptor );
			final ArrayValueExtractor<?> valueExtractor = new ArrayValueExtractor<>( valueConverter );
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) valueConverter );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) valueExtractor );
			return (JandexValueConverter<V>) valueConverter;
		}

		if ( isBoolean( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) BooleanValueConverter.JANDEX_BOOLEAN_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) BooleanValueExtractor.JANDEX_BOOLEAN_EXTRACTOR );
			return (JandexValueConverter<V>) BooleanValueConverter.JANDEX_BOOLEAN_VALUE_WRAPPER;
		}

		if ( isByte( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) ByteValueConverter.JANDEX_BYTE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) ByteValueExtractor.JANDEX_BYTE_EXTRACTOR );
			return (JandexValueConverter<V>) ByteValueConverter.JANDEX_BYTE_VALUE_WRAPPER;
		}

		if ( isChar( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) CharacterValueConverter.JANDEX_CHARACTER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) CharacterValueExtractor.JANDEX_CHARACTER_EXTRACTOR );
			return (JandexValueConverter<V>) CharacterValueConverter.JANDEX_CHARACTER_VALUE_WRAPPER;
		}

		if ( isDouble( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) DoubleValueConverter.JANDEX_DOUBLE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) DoubleValueExtractor.JANDEX_DOUBLE_EXTRACTOR );
			return (JandexValueConverter<V>) DoubleValueConverter.JANDEX_DOUBLE_VALUE_WRAPPER;
		}

		if ( isFloat( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) FloatValueConverter.JANDEX_FLOAT_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) FloatValueExtractor.JANDEX_FLOAT_EXTRACTOR );
			return (JandexValueConverter<V>) FloatValueConverter.JANDEX_FLOAT_VALUE_WRAPPER;
		}

		if ( isInt( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) IntegerValueConverter.JANDEX_INTEGER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) IntegerValueExtractor.JANDEX_INTEGER_EXTRACTOR );
			return (JandexValueConverter<V>) IntegerValueConverter.JANDEX_INTEGER_VALUE_WRAPPER;
		}

		if ( isLong( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) LongValueConverter.JANDEX_LONG_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) LongValueExtractor.JANDEX_LONG_EXTRACTOR );
			return (JandexValueConverter<V>) LongValueConverter.JANDEX_LONG_VALUE_WRAPPER;
		}

		if ( isShort( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) ShortValueConverter.JANDEX_SHORT_VALUE_WRAPPER  );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) ShortValueExtractor.JANDEX_SHORT_EXTRACTOR );
			return (JandexValueConverter<V>) ShortValueConverter.JANDEX_SHORT_VALUE_WRAPPER;
		}

		if ( valueTypeDescriptor.getValueType() == String.class ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) StringValueConverter.JANDEX_STRING_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) StringValueExtractor.JANDEX_STRING_EXTRACTOR );
			return (JandexValueConverter<V>) StringValueConverter.JANDEX_STRING_VALUE_WRAPPER;
		}

		if ( valueTypeDescriptor.getValueType().isAnnotation() ) {
			final AnnotationDescriptor<? extends Annotation> annotationDescriptor = buildingContext.getAnnotationDescriptorRegistry()
					.getDescriptor( (Class<? extends Annotation>) valueTypeDescriptor.getValueType() );
			final JandexNestedValueConverter<? extends Annotation> jandexNestedValueConverter = new JandexNestedValueConverter<>( annotationDescriptor );
			final JandexNestedValueExtractor<? extends Annotation> jandexNestedValueExtractor = new JandexNestedValueExtractor<>( jandexNestedValueConverter );

			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) jandexNestedValueConverter );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) jandexNestedValueExtractor );
			return (JandexValueConverter<V>) jandexNestedValueConverter;
		}

		if ( valueTypeDescriptor.getValueType().isEnum() ) {
			//noinspection rawtypes
			final EnumValueConverter<? extends Enum> converter = new EnumValueConverter( valueTypeDescriptor.getValueType() );
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) converter );
			//noinspection rawtypes
			extractorCollector.accept( valueTypeDescriptor, new EnumValueExtractor( converter ) );
			return (JandexValueConverter<V>) converter;
		}

		if ( valueTypeDescriptor.getValueType() == Class.class ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) ClassValueConverter.JANDEX_CLASS_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) ClassValueExtractor.JANDEX_CLASS_EXTRACTOR );
			return (JandexValueConverter<V>) ClassValueConverter.JANDEX_CLASS_VALUE_WRAPPER;
		}

		throw new UnsupportedOperationException( "Unhandled value type : " + valueTypeDescriptor );
	}

	@SuppressWarnings("unchecked")
	public static <V> JandexValueExtractor<V> buildValueHandlersReturnExtractor(
			ValueTypeDescriptor<V> valueTypeDescriptor,
			BiConsumer<ValueTypeDescriptor<V>,JandexValueConverter<V>> converterCollector,
			BiConsumer<ValueTypeDescriptor<V>,JandexValueExtractor<V>> extractorCollector,
			SourceModelBuildingContext buildingContext) {
		if ( valueTypeDescriptor.getValueType().isArray() ) {
			final ValueTypeDescriptor<?> elementTypeDescriptor = ( (ArrayTypeDescriptor<?>) valueTypeDescriptor ).getElementTypeDescriptor();
			final ArrayValueConverter<?> valueConverter = new ArrayValueConverter<>( elementTypeDescriptor );
			final ArrayValueExtractor<?> valueExtractor = new ArrayValueExtractor<>( valueConverter );
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) valueConverter );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) valueExtractor );
			return (JandexValueExtractor<V>) valueExtractor;
		}

		if ( isBoolean( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) BooleanValueConverter.JANDEX_BOOLEAN_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) BooleanValueExtractor.JANDEX_BOOLEAN_EXTRACTOR );
			return (JandexValueExtractor<V>) BooleanValueExtractor.JANDEX_BOOLEAN_EXTRACTOR;
		}

		if ( isByte( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) ByteValueConverter.JANDEX_BYTE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) ByteValueExtractor.JANDEX_BYTE_EXTRACTOR );
			return (JandexValueExtractor<V>) ByteValueExtractor.JANDEX_BYTE_EXTRACTOR;
		}

		if ( isChar( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) CharacterValueConverter.JANDEX_CHARACTER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) CharacterValueExtractor.JANDEX_CHARACTER_EXTRACTOR );
			return (JandexValueExtractor<V>) CharacterValueExtractor.JANDEX_CHARACTER_EXTRACTOR;
		}

		if ( isDouble( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) DoubleValueConverter.JANDEX_DOUBLE_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) DoubleValueExtractor.JANDEX_DOUBLE_EXTRACTOR );
			return (JandexValueExtractor<V>) DoubleValueConverter.JANDEX_DOUBLE_VALUE_WRAPPER;
		}

		if ( isFloat( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) FloatValueConverter.JANDEX_FLOAT_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) FloatValueExtractor.JANDEX_FLOAT_EXTRACTOR );
			return (JandexValueExtractor<V>) FloatValueExtractor.JANDEX_FLOAT_EXTRACTOR;
		}

		if ( isInt( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) IntegerValueConverter.JANDEX_INTEGER_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) IntegerValueExtractor.JANDEX_INTEGER_EXTRACTOR );
			return (JandexValueExtractor<V>) IntegerValueExtractor.JANDEX_INTEGER_EXTRACTOR;
		}

		if ( isLong( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) LongValueConverter.JANDEX_LONG_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) LongValueExtractor.JANDEX_LONG_EXTRACTOR );
			return (JandexValueExtractor<V>) LongValueExtractor.JANDEX_LONG_EXTRACTOR;
		}

		if ( isShort( valueTypeDescriptor ) ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) ShortValueConverter.JANDEX_SHORT_VALUE_WRAPPER  );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) ShortValueExtractor.JANDEX_SHORT_EXTRACTOR );
			return (JandexValueExtractor<V>) ShortValueExtractor.JANDEX_SHORT_EXTRACTOR;
		}

		if ( valueTypeDescriptor.getValueType() == String.class ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) StringValueConverter.JANDEX_STRING_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) StringValueExtractor.JANDEX_STRING_EXTRACTOR );
			return (JandexValueExtractor<V>) StringValueExtractor.JANDEX_STRING_EXTRACTOR;
		}

		if ( valueTypeDescriptor.getValueType().isAnnotation() ) {
			final AnnotationDescriptor<? extends Annotation> annotationDescriptor = buildingContext.getAnnotationDescriptorRegistry()
					.getDescriptor( (Class<? extends Annotation>) valueTypeDescriptor.getValueType() );
			final JandexNestedValueConverter<? extends Annotation> jandexNestedValueConverter = new JandexNestedValueConverter<>( annotationDescriptor );
			final JandexNestedValueExtractor<? extends Annotation> jandexNestedValueExtractor = new JandexNestedValueExtractor<>( jandexNestedValueConverter );

			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) jandexNestedValueConverter );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) jandexNestedValueExtractor );
			return (JandexValueExtractor<V>) jandexNestedValueExtractor;
		}

		if ( valueTypeDescriptor.getValueType().isEnum() ) {
			//noinspection rawtypes
			final EnumValueConverter<? extends Enum> converter = new EnumValueConverter( valueTypeDescriptor.getValueType() );
			//noinspection rawtypes
			final EnumValueExtractor extractor = new EnumValueExtractor( converter );
			converterCollector.accept( valueTypeDescriptor, (JandexValueConverter<V>) converter );
			extractorCollector.accept( valueTypeDescriptor, extractor );
			return (JandexValueExtractor<V>) extractor;
		}

		if ( valueTypeDescriptor.getValueType() == Class.class ) {
			converterCollector.accept( valueTypeDescriptor,  (JandexValueConverter<V>) ClassValueConverter.JANDEX_CLASS_VALUE_WRAPPER );
			extractorCollector.accept( valueTypeDescriptor, (JandexValueExtractor<V>) ClassValueExtractor.JANDEX_CLASS_EXTRACTOR );
			return (JandexValueExtractor<V>) ClassValueExtractor.JANDEX_CLASS_EXTRACTOR;
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
