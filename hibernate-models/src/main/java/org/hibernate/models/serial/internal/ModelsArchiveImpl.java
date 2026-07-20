/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.hibernate.models.internal.ArrayTypeDetailsImpl;
import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.ParameterizedTypeDetailsImpl;
import org.hibernate.models.internal.PrimitiveTypeDetailsImpl;
import org.hibernate.models.internal.TypeVariableReferenceDetailsImpl;
import org.hibernate.models.internal.VoidTypeDetailsImpl;
import org.hibernate.models.internal.WildcardTypeDetailsImpl;
import org.hibernate.models.serial.spi.ModelReference;
import org.hibernate.models.serial.spi.ModelsArchive;
import org.hibernate.models.serial.spi.RestoredModels;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ConstructorDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.MutableAnnotationTarget;
import org.hibernate.models.spi.MutableAnnotationDescriptor;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.TypeVariableDetails;
import org.hibernate.models.spi.TypeVariableScope;

/// Standard versioned models archive.
///
/// @author Steve Ebersole
public class ModelsArchiveImpl implements ModelsArchive {
	private static final int MAGIC = 0x484D4F44;
	private static final int FORMAT_VERSION = 1;
	private static final int MAX_TABLE_SIZE = 1_000_000;

	private boolean trackImplementors;
	private List<SerialClassDetails> classes;
	private List<TypeReference> types;
	private List<FieldReference> fields;
	private List<MethodReference> methods;
	private List<ConstructorReference> constructors;
	private List<RecordComponentReference> recordComponents;
	private List<AnnotationUsageReference> annotationUsages;

	/**
	 * Required by {@link java.io.Externalizable}.
	 */
	public ModelsArchiveImpl() {
	}

	public ModelsArchiveImpl(
			boolean trackImplementors,
			List<SerialClassDetails> classes,
			List<TypeReference> types,
			List<FieldReference> fields,
			List<MethodReference> methods,
			List<ConstructorReference> constructors,
			List<RecordComponentReference> recordComponents,
			List<AnnotationUsageReference> annotationUsages) {
		this.trackImplementors = trackImplementors;
		this.classes = List.copyOf( classes );
		this.types = List.copyOf( types );
		this.fields = List.copyOf( fields );
		this.methods = List.copyOf( methods );
		this.constructors = List.copyOf( constructors );
		this.recordComponents = List.copyOf( recordComponents );
		this.annotationUsages = List.copyOf( annotationUsages );
		validateArchiveState();
	}

	@Override
	public void writeExternal(ObjectOutput output) throws IOException {
		checkInitialized();
		output.writeInt( MAGIC );
		output.writeInt( FORMAT_VERSION );
		output.writeBoolean( trackImplementors );
		output.writeInt( classes.size() );
		for ( SerialClassDetails serialClass : classes ) {
			output.writeObject( serialClass );
		}
		writeTable( output, types );
		writeTable( output, fields );
		writeTable( output, methods );
		writeTable( output, constructors );
		writeTable( output, recordComponents );
		writeTable( output, annotationUsages );
	}

	@Override
	public void readExternal(ObjectInput input) throws IOException, ClassNotFoundException {
		final int magic = input.readInt();
		if ( magic != MAGIC ) {
			throw new InvalidObjectException( "Unexpected Hibernate Models archive magic: " + magic );
		}

		final int version = input.readInt();
		if ( version != FORMAT_VERSION ) {
			throw new InvalidObjectException( "Unsupported Hibernate Models archive version: " + version );
		}

		trackImplementors = input.readBoolean();
		final int classCount = input.readInt();
		if ( classCount < 0 || classCount > MAX_TABLE_SIZE ) {
			throw new InvalidObjectException( "Invalid Hibernate Models class-table size: " + classCount );
		}

		final ArrayList<SerialClassDetails> restoredClasses = new ArrayList<>( classCount );
		for ( int i = 0; i < classCount; i++ ) {
			final Object entry = input.readObject();
			if ( !( entry instanceof SerialClassDetails serialClass ) ) {
				throw new InvalidObjectException( "Invalid class-table entry at index " + i );
			}
			validateClassEntry( serialClass, i );
			restoredClasses.add( serialClass );
		}
		classes = List.copyOf( restoredClasses );
		types = readTable( input, TypeReference.class, "type" );
		fields = readTable( input, FieldReference.class, "field" );
		methods = readTable( input, MethodReference.class, "method" );
		constructors = readTable( input, ConstructorReference.class, "constructor" );
		recordComponents = readTable( input, RecordComponentReference.class, "record-component" );
		annotationUsages = readTable( input, AnnotationUsageReference.class, "annotation-usage" );
	}

	@Override
	public RestoredModels restore(ClassLoading classLoading, RegistryPrimer registryPrimer) {
		checkInitialized();
		final LinkedHashMap<String, SerialClassDetails> serialClasses = new LinkedHashMap<>();
		for ( SerialClassDetails serialClass : classes ) {
			final SerialClassDetails previous = serialClasses.put( serialClass.getName(), serialClass );
			if ( previous != null ) {
				throw new IllegalStateException( "Duplicate archived class name: " + serialClass.getName() );
			}
		}

		final ModelsContext modelsContext = new RestoredModelContext(
				serialClasses,
				classLoading,
				trackImplementors,
				registryPrimer
		);
		final List<ClassDetails> restoredClasses = new ArrayList<>( classes.size() );
		for ( SerialClassDetails serialClass : classes ) {
			restoredClasses.add( modelsContext.getClassDetailsRegistry().findClassDetails( serialClass.getName() ) );
		}
		final List<TypeDetails> restoredTypes = restoreTypes( restoredClasses );
		final List<FieldDetails> restoredFields = restoreFields( restoredClasses );
		final List<MethodDetails> restoredMethods = restoreMethods( restoredClasses );
		final List<ConstructorDetails> restoredConstructors = restoreConstructors( restoredClasses );
		final List<RecordComponentDetails> restoredRecordComponents = restoreRecordComponents( restoredClasses );
		final RestoredModelsImpl restoredModels = new RestoredModelsImpl(
				modelsContext,
				restoredClasses,
				restoredTypes,
				restoredFields,
				restoredMethods,
				restoredConstructors,
				restoredRecordComponents
		);
		restoreAnnotationUsages( restoredModels );
		return restoredModels;
	}

	private void checkInitialized() {
		if ( classes == null
				|| types == null
				|| fields == null
				|| methods == null
				|| constructors == null
				|| recordComponents == null
				|| annotationUsages == null ) {
			throw new IllegalStateException( "Hibernate Models archive is not initialized" );
		}
	}

	private void validateArchiveState() {
		for ( int i = 0; i < classes.size(); i++ ) {
			try {
				validateClassEntry( classes.get( i ), i );
			}
			catch (InvalidObjectException e) {
				throw new IllegalArgumentException( e.getMessage(), e );
			}
		}
		validateTable( types, "type" );
		validateTable( fields, "field" );
		validateTable( methods, "method" );
		validateTable( constructors, "constructor" );
		validateTable( recordComponents, "record-component" );
		validateTable( annotationUsages, "annotation-usage" );
	}

	private static void validateTable(List<?> table, String tableName) {
		Objects.requireNonNull( table, tableName );
		for ( int i = 0; i < table.size(); i++ ) {
			final Object entry = table.get( i );
			if ( entry == null ) {
				throw new IllegalArgumentException( "Invalid " + tableName + "-table entry at index " + i + ": null" );
			}
			try {
				validateTableEntry( entry, tableName, i );
			}
			catch (InvalidObjectException e) {
				throw new IllegalArgumentException( e.getMessage(), e );
			}
		}
	}

	private static void writeTable(ObjectOutput output, List<? extends Serializable> table) throws IOException {
		Objects.requireNonNull( table, "table" );
		output.writeInt( table.size() );
		for ( int i = 0; i < table.size(); i++ ) {
			final Serializable entry = table.get( i );
			if ( entry == null ) {
				throw new InvalidObjectException( "Archive table contains a null entry at index " + i );
			}
			output.writeObject( entry );
		}
	}

	private static <T> List<T> readTable(ObjectInput input, Class<T> entryType, String tableName)
			throws IOException, ClassNotFoundException {
		final int entryCount = input.readInt();
		if ( entryCount < 0 || entryCount > MAX_TABLE_SIZE ) {
			throw new InvalidObjectException( "Invalid Hibernate Models " + tableName + "-table size: " + entryCount );
		}

		final ArrayList<T> table = new ArrayList<>( entryCount );
		for ( int i = 0; i < entryCount; i++ ) {
			final Object entry = input.readObject();
			if ( !entryType.isInstance( entry ) ) {
				throw new InvalidObjectException( "Invalid " + tableName + "-table entry at index " + i );
			}
			validateTableEntry( entry, tableName, i );
			table.add( entryType.cast( entry ) );
		}
		return List.copyOf( table );
	}

	private static void validateClassEntry(SerialClassDetails serialClass, int index) throws InvalidObjectException {
		if ( serialClass.getName() == null || serialClass.getName().isBlank() ) {
			throw new InvalidObjectException( "Invalid class-table entry name at index " + index );
		}
	}

	private static void validateTableEntry(Object entry, String tableName, int index) throws InvalidObjectException {
		try {
			if ( entry instanceof TypeReference typeReference ) {
				validateTypeReference( typeReference );
			}
			else if ( entry instanceof FieldReference fieldReference ) {
				validateClassId( fieldReference.declaringClassId(), "field declaring class" );
				validateName( fieldReference.name(), "field name" );
			}
			else if ( entry instanceof MethodReference methodReference ) {
				validateClassId( methodReference.declaringClassId(), "method declaring class" );
				validateName( methodReference.name(), "method name" );
				validateNameList( methodReference.argumentTypeNames(), "method argument type names" );
			}
			else if ( entry instanceof ConstructorReference constructorReference ) {
				validateClassId( constructorReference.declaringClassId(), "constructor declaring class" );
				validateNameList( constructorReference.argumentTypeNames(), "constructor argument type names" );
			}
			else if ( entry instanceof RecordComponentReference recordComponentReference ) {
				validateClassId( recordComponentReference.declaringClassId(), "record-component declaring class" );
				validateName( recordComponentReference.name(), "record-component name" );
			}
			else if ( entry instanceof AnnotationUsageReference annotationUsageReference ) {
				validateAnnotationUsageReference( annotationUsageReference );
			}
		}
		catch (IllegalArgumentException e) {
			final InvalidObjectException invalidObjectException = new InvalidObjectException(
					"Invalid " + tableName + "-table entry at index " + index + ": " + e.getMessage()
			);
			invalidObjectException.initCause( e );
			throw invalidObjectException;
		}
	}

	private static void validateTypeReference(TypeReference typeReference) {
		if ( typeReference instanceof ClassTypeReference classTypeReference ) {
			Objects.requireNonNull( classTypeReference.kind(), "type kind" );
			validateClassId( classTypeReference.classId(), "class type class" );
		}
		else if ( typeReference instanceof ArrayTypeReference arrayTypeReference ) {
			validateClassId( arrayTypeReference.arrayClassId(), "array type class" );
			validateTypeId( arrayTypeReference.constituentTypeId(), "array constituent type" );
		}
		else if ( typeReference instanceof ParameterizedTypeReference parameterizedTypeReference ) {
			validateClassId( parameterizedTypeReference.rawClassId(), "parameterized raw class" );
			validateIdList( parameterizedTypeReference.argumentTypeIds(), "parameterized argument type ids" );
			validateScopeReference( parameterizedTypeReference.owner() );
		}
		else if ( typeReference instanceof TypeVariableReference typeVariableReference ) {
			validateName( typeVariableReference.identifier(), "type-variable identifier" );
			validateClassId( typeVariableReference.declaringClassId(), "type-variable declaring class" );
			validateIdList( typeVariableReference.boundTypeIds(), "type-variable bound type ids" );
		}
		else if ( typeReference instanceof TypeVariableTargetReference typeVariableTargetReference ) {
			validateName( typeVariableTargetReference.identifier(), "type-variable-reference identifier" );
			validateTypeId( typeVariableTargetReference.targetTypeId(), "type-variable-reference target type" );
		}
		else if ( typeReference instanceof WildcardTypeReference wildcardTypeReference
				&& wildcardTypeReference.boundTypeId() < -1 ) {
			throw new IllegalArgumentException( "wildcard bound type id is less than -1: " + wildcardTypeReference.boundTypeId() );
		}
	}

	private static void validateAnnotationUsageReference(AnnotationUsageReference annotationUsageReference) {
		Objects.requireNonNull( annotationUsageReference.target(), "annotation target reference" );
		validateName( annotationUsageReference.annotationTypeName(), "annotation type name" );
		if ( annotationUsageReference.mutableContractName() != null ) {
			validateName( annotationUsageReference.mutableContractName(), "mutable annotation contract name" );
		}
		Objects.requireNonNull( annotationUsageReference.values(), "annotation values" );
		for ( Map.Entry<String, AnnotationValueReference> valueEntry : annotationUsageReference.values().entrySet() ) {
			validateName( valueEntry.getKey(), "annotation attribute name" );
			validateAnnotationValueReference( valueEntry.getValue() );
		}
	}

	private static void validateAnnotationValueReference(AnnotationValueReference annotationValueReference) {
		Objects.requireNonNull( annotationValueReference, "annotation value reference" );
		if ( annotationValueReference instanceof BasicAnnotationValueReference basicValue
				&& basicValue.value() != null
				&& !( basicValue.value() instanceof String
						|| basicValue.value() instanceof Boolean
						|| basicValue.value() instanceof Byte
						|| basicValue.value() instanceof Short
						|| basicValue.value() instanceof Integer
						|| basicValue.value() instanceof Long
						|| basicValue.value() instanceof Float
						|| basicValue.value() instanceof Double
						|| basicValue.value() instanceof Character ) ) {
			throw new IllegalArgumentException( "unsupported basic annotation value type: " + basicValue.value().getClass().getName() );
		}
		if ( annotationValueReference instanceof EnumAnnotationValueReference enumValue ) {
			validateName( enumValue.enumTypeName(), "enum type name" );
			validateName( enumValue.constantName(), "enum constant name" );
		}
		else if ( annotationValueReference instanceof ClassAnnotationValueReference classValue ) {
			validateClassId( classValue.classId(), "class annotation value" );
		}
		else if ( annotationValueReference instanceof NestedAnnotationValueReference nestedValue ) {
			validateName( nestedValue.annotationTypeName(), "nested annotation type name" );
			Objects.requireNonNull( nestedValue.values(), "nested annotation values" );
			for ( Map.Entry<String, AnnotationValueReference> valueEntry : nestedValue.values().entrySet() ) {
				validateName( valueEntry.getKey(), "nested annotation attribute name" );
				validateAnnotationValueReference( valueEntry.getValue() );
			}
		}
		else if ( annotationValueReference instanceof ArrayAnnotationValueReference arrayValue ) {
			validateName( arrayValue.componentTypeName(), "array annotation component type name" );
			Objects.requireNonNull( arrayValue.values(), "array annotation values" );
			arrayValue.values().forEach( ModelsArchiveImpl::validateAnnotationValueReference );
		}
	}

	private static void validateScopeReference(ScopeReference scopeReference) {
		if ( scopeReference == null ) {
			return;
		}
		Objects.requireNonNull( scopeReference.kind(), "scope reference kind" );
		switch ( scopeReference.kind() ) {
			case CLASS -> validateClassId( scopeReference.id(), "scope class" );
			case TYPE -> validateTypeId( scopeReference.id(), "scope type" );
			default -> throw new IllegalArgumentException( "invalid scope reference kind: " + scopeReference.kind() );
		}
	}

	private static void validateIdList(List<Integer> ids, String label) {
		Objects.requireNonNull( ids, label );
		ids.forEach( id -> {
			Objects.requireNonNull( id, label );
			validateTypeId( id, label );
		} );
	}

	private static void validateNameList(List<String> names, String label) {
		Objects.requireNonNull( names, label );
		names.forEach( name -> validateName( name, label ) );
	}

	private static void validateClassId(int id, String label) {
		if ( id < 0 ) {
			throw new IllegalArgumentException( label + " id is negative: " + id );
		}
	}

	private static void validateTypeId(int id, String label) {
		if ( id < 0 ) {
			throw new IllegalArgumentException( label + " id is negative: " + id );
		}
	}

	private static void validateName(String name, String label) {
		if ( name == null || name.isBlank() ) {
			throw new IllegalArgumentException( label + " is null or blank" );
		}
	}

	private List<FieldDetails> restoreFields(List<ClassDetails> restoredClasses) {
		final ArrayList<FieldDetails> restoredFields = new ArrayList<>( fields.size() );
		for ( FieldReference field : fields ) {
			final ClassDetails declaringType = resolveDeclaringType( restoredClasses, field.declaringClassId() );
			final FieldDetails restoredField = declaringType.findFieldByName( field.name() );
			if ( restoredField == null ) {
				throw new IllegalStateException(
						"Could not locate archived field `%s` on `%s`".formatted( field.name(), declaringType.getName() )
				);
			}
			restoredFields.add( restoredField );
		}
		return List.copyOf( restoredFields );
	}

	private List<TypeDetails> restoreTypes(List<ClassDetails> restoredClasses) {
		final ArrayList<TypeDetails> restoredTypes = new ArrayList<>( types.size() );
		for ( int i = 0; i < types.size(); i++ ) {
			restoredTypes.add( null );
		}
		for ( int i = 0; i < types.size(); i++ ) {
			restoreType( i, restoredClasses, restoredTypes );
		}
		return List.copyOf( restoredTypes );
	}

	private TypeDetails restoreType(
			int typeId,
			List<ClassDetails> restoredClasses,
			ArrayList<TypeDetails> restoredTypes) {
		if ( typeId < 0 || typeId >= types.size() ) {
			throw new IllegalStateException( "Type reference id is out of range: " + typeId );
		}

		final TypeDetails existing = restoredTypes.get( typeId );
		if ( existing != null ) {
			return existing;
		}

		final TypeReference reference = types.get( typeId );
		if ( reference instanceof TypeVariableReference typeVariableReference ) {
			final RestoredTypeVariableDetails placeholder = new RestoredTypeVariableDetails(
					typeVariableReference.identifier(),
					resolveDeclaringType( restoredClasses, typeVariableReference.declaringClassId() )
			);
			restoredTypes.set( typeId, placeholder );
			placeholder.setBounds( typeVariableReference.boundTypeIds()
					.stream()
					.map( boundTypeId -> restoreType( boundTypeId, restoredClasses, restoredTypes ) )
					.toList() );
			return placeholder;
		}

		final TypeDetails restoredType;
		if ( reference instanceof ClassTypeReference classTypeReference ) {
			restoredType = restoreClassType( classTypeReference, restoredClasses );
		}
		else if ( reference instanceof ArrayTypeReference arrayTypeReference ) {
			restoredType = new ArrayTypeDetailsImpl(
					resolveDeclaringType( restoredClasses, arrayTypeReference.arrayClassId() ),
					restoreType( arrayTypeReference.constituentTypeId(), restoredClasses, restoredTypes )
			);
		}
		else if ( reference instanceof ParameterizedTypeReference parameterizedTypeReference ) {
			restoredType = new ParameterizedTypeDetailsImpl(
					resolveDeclaringType( restoredClasses, parameterizedTypeReference.rawClassId() ),
					parameterizedTypeReference.argumentTypeIds()
							.stream()
							.map( argumentTypeId -> restoreType( argumentTypeId, restoredClasses, restoredTypes ) )
							.toList(),
					restoreScope( parameterizedTypeReference.owner(), restoredClasses, restoredTypes )
			);
		}
		else if ( reference instanceof TypeVariableTargetReference typeVariableTargetReference ) {
			restoredType = new TypeVariableReferenceDetailsImpl(
					typeVariableTargetReference.identifier(),
					restoreTypeVariable( typeVariableTargetReference.targetTypeId(), restoredClasses, restoredTypes )
			);
		}
		else if ( reference instanceof WildcardTypeReference wildcardTypeReference ) {
			restoredType = new WildcardTypeDetailsImpl(
					wildcardTypeReference.boundTypeId() < 0
							? null
							: restoreType( wildcardTypeReference.boundTypeId(), restoredClasses, restoredTypes ),
					wildcardTypeReference.isExtends()
			);
		}
		else {
			throw new IllegalStateException( "Type variable should be handled earlier" );
		}
		restoredTypes.set( typeId, restoredType );
		return restoredType;
	}

	private static TypeDetails restoreClassType(ClassTypeReference reference, List<ClassDetails> restoredClasses) {
		final ClassDetails classDetails = resolveDeclaringType( restoredClasses, reference.classId() );
		return switch ( reference.kind() ) {
			case CLASS -> new ClassTypeDetailsImpl( classDetails, TypeDetails.Kind.CLASS );
			case PRIMITIVE -> new PrimitiveTypeDetailsImpl( classDetails );
			case VOID -> new VoidTypeDetailsImpl( classDetails );
			default -> throw new IllegalStateException( "Invalid class-based type kind: " + reference.kind() );
		};
	}

	private TypeVariableDetails restoreTypeVariable(
			int typeId,
			List<ClassDetails> restoredClasses,
			ArrayList<TypeDetails> restoredTypes) {
		final TypeDetails restoredType = restoreType( typeId, restoredClasses, restoredTypes );
		if ( restoredType instanceof TypeVariableDetails typeVariableDetails ) {
			return typeVariableDetails;
		}
		throw new IllegalStateException( "Type reference does not resolve to a type variable: " + typeId );
	}

	private TypeVariableScope restoreScope(
			ScopeReference scopeReference,
			List<ClassDetails> restoredClasses,
			ArrayList<TypeDetails> restoredTypes) {
		if ( scopeReference == null ) {
			return null;
		}
		return switch ( scopeReference.kind() ) {
			case CLASS -> resolveDeclaringType( restoredClasses, scopeReference.id() );
			case TYPE -> restoreType( scopeReference.id(), restoredClasses, restoredTypes );
			default -> throw new IllegalStateException( "Invalid type-variable scope kind: " + scopeReference.kind() );
		};
	}

	private void restoreAnnotationUsages(RestoredModelsImpl restoredModels) {
		for ( AnnotationUsageReference annotationUsage : annotationUsages ) {
			final Object target = restoredModels.resolve( annotationUsage.target() );
			if ( !( target instanceof MutableAnnotationTarget mutableTarget ) ) {
				throw new IllegalStateException( "Archived annotation target is not mutable: " + target );
			}
			mutableTarget.addAnnotationUsage( restoreAnnotationUsage( annotationUsage, restoredModels ) );
		}
	}

	private Annotation restoreAnnotationUsage(AnnotationUsageReference annotationUsage, RestoredModelsImpl restoredModels) {
		final ModelsContext modelsContext = restoredModels.modelsContext();
		final Class<? extends Annotation> annotationType = resolveAnnotationType( annotationUsage.annotationTypeName(), modelsContext );
		final AnnotationDescriptor<? extends Annotation> descriptor = modelsContext.getAnnotationDescriptorRegistry()
				.getDescriptor( annotationType );
		validateDescriptorRequirement( annotationUsage, descriptor );
		return restoreAnnotationUsage( descriptor, annotationUsage.values(), restoredModels );
	}

	private void validateDescriptorRequirement(
			AnnotationUsageReference annotationUsage,
			AnnotationDescriptor<? extends Annotation> descriptor) {
		if ( annotationUsage.mutableContractName() == null ) {
			return;
		}
		if ( !( descriptor instanceof MutableAnnotationDescriptor<?, ?> mutableDescriptor ) ) {
			throw new IllegalStateException(
					"Archived annotation `%s` requires mutable contract `%s`, but the restored descriptor is not mutable"
							.formatted( annotationUsage.annotationTypeName(), annotationUsage.mutableContractName() )
			);
		}
		if ( !mutableDescriptor.getMutableAnnotationType().getName().equals( annotationUsage.mutableContractName() ) ) {
			throw new IllegalStateException(
					"Archived annotation `%s` requires mutable contract `%s`, but the restored descriptor provides `%s`"
							.formatted(
									annotationUsage.annotationTypeName(),
									annotationUsage.mutableContractName(),
									mutableDescriptor.getMutableAnnotationType().getName()
							)
			);
		}
	}

	private <A extends Annotation> A restoreAnnotationUsage(
			AnnotationDescriptor<A> descriptor,
			Map<String, AnnotationValueReference> values,
			RestoredModelsImpl restoredModels) {
		final LinkedHashMap<String, Object> restoredValues = new LinkedHashMap<>();
		for ( AttributeDescriptor<?> attribute : descriptor.getAttributes() ) {
			final AnnotationValueReference value = values.get( attribute.getName() );
			if ( value == null ) {
				throw new IllegalStateException(
						"Missing archived annotation attribute `%s.%s`".formatted(
								descriptor.getAnnotationType().getName(),
								attribute.getName()
						)
				);
			}
			restoredValues.put(
					attribute.getName(),
					restoreAnnotationValue( value, attribute.getAttributeMethod().getReturnType(), restoredModels )
			);
		}
		return descriptor.createUsage( restoredValues, restoredModels.modelsContext() );
	}

	private Object restoreAnnotationValue(
			AnnotationValueReference value,
			Class<?> declaredType,
			RestoredModelsImpl restoredModels) {
		final ModelsContext modelsContext = restoredModels.modelsContext();
		if ( value instanceof NullAnnotationValueReference ) {
			return null;
		}
		if ( value instanceof BasicAnnotationValueReference basicValue ) {
			return basicValue.value();
		}
		if ( value instanceof ClassAnnotationValueReference classValue ) {
			final ClassDetails classDetails = (ClassDetails) restoredModels.resolve(
					new ModelReference( ModelReference.Kind.CLASS, classValue.classId() )
			);
			return classDetails.toJavaClass( modelsContext.getClassLoading(), modelsContext );
		}
		if ( value instanceof EnumAnnotationValueReference enumValue ) {
			return restoreEnumValue( enumValue, modelsContext );
		}
		if ( value instanceof NestedAnnotationValueReference nestedValue ) {
			final Class<? extends Annotation> annotationType = resolveAnnotationType( nestedValue.annotationTypeName(), modelsContext );
			final AnnotationDescriptor<? extends Annotation> descriptor = modelsContext.getAnnotationDescriptorRegistry()
					.getDescriptor( annotationType );
			return restoreAnnotationUsage( descriptor, nestedValue.values(), restoredModels );
		}
		if ( value instanceof ArrayAnnotationValueReference arrayValue ) {
			final Class<?> componentType = declaredType.getComponentType();
			if ( componentType == null ) {
				throw new IllegalStateException(
						"Archived annotation value is an array, but declared type is not: " + declaredType.getName()
				);
			}
			final Object array = Array.newInstance( componentType, arrayValue.values().size() );
			for ( int i = 0; i < arrayValue.values().size(); i++ ) {
				Array.set( array, i, restoreAnnotationValue( arrayValue.values().get( i ), componentType, restoredModels ) );
			}
			return array;
		}
		throw new IllegalStateException( "Unknown annotation value reference: " + value );
	}

	private Enum<?> restoreEnumValue(EnumAnnotationValueReference enumValue, ModelsContext modelsContext) {
		final Class<?> enumType;
		try {
			enumType = modelsContext.getClassLoading().classForName( enumValue.enumTypeName() );
		}
		catch (RuntimeException e) {
			throw new IllegalStateException(
					"Could not resolve archived enum annotation value type `%s`".formatted( enumValue.enumTypeName() ),
					e
			);
		}
		if ( !enumType.isEnum() ) {
			throw new IllegalStateException( "Archived enum annotation value does not name an enum: " + enumValue.enumTypeName() );
		}
		for ( Object enumConstant : enumType.getEnumConstants() ) {
			final Enum<?> enumConstantValue = (Enum<?>) enumConstant;
			if ( enumConstantValue.name().equals( enumValue.constantName() ) ) {
				return enumConstantValue;
			}
		}
		throw new IllegalStateException(
				"Could not resolve archived enum annotation value `%s.%s`".formatted(
						enumValue.enumTypeName(),
						enumValue.constantName()
				)
		);
	}

	private Class<? extends Annotation> resolveAnnotationType(String annotationTypeName, ModelsContext modelsContext) {
		final Class<?> annotationType = modelsContext.getClassLoading().classForName( annotationTypeName );
		if ( !annotationType.isAnnotation() ) {
			throw new IllegalStateException( "Archived annotation type is not an annotation: " + annotationTypeName );
		}
		//noinspection unchecked
		return (Class<? extends Annotation>) annotationType;
	}

	private List<MethodDetails> restoreMethods(List<ClassDetails> restoredClasses) {
		final ArrayList<MethodDetails> restoredMethods = new ArrayList<>( methods.size() );
		for ( MethodReference method : methods ) {
			final ClassDetails declaringType = resolveDeclaringType( restoredClasses, method.declaringClassId() );
			restoredMethods.add( declaringType.getMethods().stream()
					.filter( candidate -> matches( candidate, method ) )
					.findFirst()
					.orElseThrow( () -> new IllegalStateException(
							"Could not locate archived method `%s` on `%s`".formatted(
									method.name(),
									declaringType.getName()
							)
					) ) );
		}
		return List.copyOf( restoredMethods );
	}

	private List<ConstructorDetails> restoreConstructors(List<ClassDetails> restoredClasses) {
		final ArrayList<ConstructorDetails> restoredConstructors = new ArrayList<>( constructors.size() );
		for ( ConstructorReference constructor : constructors ) {
			final ClassDetails declaringType = resolveDeclaringType( restoredClasses, constructor.declaringClassId() );
			restoredConstructors.add( declaringType.getConstructors().stream()
					.filter( candidate -> matches( candidate, constructor ) )
					.findFirst()
					.orElseThrow( () -> new IllegalStateException(
							"Could not locate archived constructor on `%s`".formatted( declaringType.getName() )
					) ) );
		}
		return List.copyOf( restoredConstructors );
	}

	private List<RecordComponentDetails> restoreRecordComponents(List<ClassDetails> restoredClasses) {
		final ArrayList<RecordComponentDetails> restoredRecordComponents = new ArrayList<>( recordComponents.size() );
		for ( RecordComponentReference recordComponent : recordComponents ) {
			final ClassDetails declaringType = resolveDeclaringType( restoredClasses, recordComponent.declaringClassId() );
			final RecordComponentDetails restoredRecordComponent = declaringType.findRecordComponentByName(
					recordComponent.name()
			);
			if ( restoredRecordComponent == null ) {
				throw new IllegalStateException(
						"Could not locate archived record component `%s` on `%s`".formatted(
								recordComponent.name(),
								declaringType.getName()
						)
				);
			}
			restoredRecordComponents.add( restoredRecordComponent );
		}
		return List.copyOf( restoredRecordComponents );
	}

	private static ClassDetails resolveDeclaringType(List<ClassDetails> restoredClasses, int declaringClassId) {
		if ( declaringClassId < 0 || declaringClassId >= restoredClasses.size() ) {
			throw new IllegalStateException( "Declaring class reference id is out of range: " + declaringClassId );
		}
		return restoredClasses.get( declaringClassId );
	}

	private static boolean matches(MethodDetails candidate, MethodReference reference) {
		return candidate.getName().equals( reference.name() )
				&& candidate.getArgumentTypes().stream().map( ClassDetails::getName ).toList()
						.equals( reference.argumentTypeNames() );
	}

	private static boolean matches(ConstructorDetails candidate, ConstructorReference reference) {
		return candidate.getArgumentTypes().stream().map( ClassDetails::getName ).toList()
				.equals( reference.argumentTypeNames() );
	}

	private record RestoredModelsImpl(
			ModelsContext modelsContext,
			List<ClassDetails> classes,
			List<TypeDetails> types,
			List<FieldDetails> fields,
			List<MethodDetails> methods,
			List<ConstructorDetails> constructors,
			List<RecordComponentDetails> recordComponents) implements RestoredModels {
		@Override
		public ModelsContext getModelsContext() {
			return modelsContext;
		}

		@Override
		public Object resolve(ModelReference reference) {
			return switch ( reference.kind() ) {
				case CLASS -> resolve( classes, reference, "Class" );
				case TYPE -> resolve( types, reference, "Type" );
				case FIELD -> resolve( fields, reference, "Field" );
				case METHOD -> resolve( methods, reference, "Method" );
				case CONSTRUCTOR -> resolve( constructors, reference, "Constructor" );
				case RECORD_COMPONENT -> resolve( recordComponents, reference, "Record component" );
				case MODULE -> throw new IllegalArgumentException( "MODULE archive entries are not implemented yet" );
			};
		}

		private static Object resolve(List<?> table, ModelReference reference, String label) {
			if ( reference.id() >= table.size() ) {
				throw new IllegalArgumentException( label + " reference id is out of range: " + reference.id() );
			}
			return table.get( reference.id() );
		}
	}

	sealed interface TypeReference extends Serializable permits ClassTypeReference, ArrayTypeReference,
			ParameterizedTypeReference, TypeVariableReference, TypeVariableTargetReference, WildcardTypeReference {
	}

	record ClassTypeReference(TypeDetails.Kind kind, int classId) implements TypeReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record ArrayTypeReference(int arrayClassId, int constituentTypeId) implements TypeReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record ParameterizedTypeReference(
			int rawClassId,
			List<Integer> argumentTypeIds,
			ScopeReference owner) implements TypeReference {
		@Serial
		private static final long serialVersionUID = 1L;

		ParameterizedTypeReference {
			argumentTypeIds = List.copyOf( argumentTypeIds );
		}
	}

	record TypeVariableReference(String identifier, int declaringClassId, List<Integer> boundTypeIds)
			implements TypeReference {
		@Serial
		private static final long serialVersionUID = 1L;

		TypeVariableReference {
			boundTypeIds = List.copyOf( boundTypeIds );
		}
	}

	record TypeVariableTargetReference(String identifier, int targetTypeId) implements TypeReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record WildcardTypeReference(int boundTypeId, boolean isExtends) implements TypeReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record ScopeReference(ModelReference.Kind kind, int id) implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record AnnotationUsageReference(
			ModelReference target,
			String annotationTypeName,
			String mutableContractName,
			Map<String, AnnotationValueReference> values) implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		AnnotationUsageReference {
			values = new LinkedHashMap<>( values );
		}
	}

	sealed interface AnnotationValueReference extends Serializable permits NullAnnotationValueReference,
			BasicAnnotationValueReference, EnumAnnotationValueReference, ClassAnnotationValueReference,
			NestedAnnotationValueReference, ArrayAnnotationValueReference {
	}

	record NullAnnotationValueReference() implements AnnotationValueReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record BasicAnnotationValueReference(Object value) implements AnnotationValueReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record EnumAnnotationValueReference(String enumTypeName, String constantName) implements AnnotationValueReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record ClassAnnotationValueReference(int classId) implements AnnotationValueReference {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record NestedAnnotationValueReference(
			String annotationTypeName,
			Map<String, AnnotationValueReference> values) implements AnnotationValueReference {
		@Serial
		private static final long serialVersionUID = 1L;

		NestedAnnotationValueReference {
			values = new LinkedHashMap<>( values );
		}
	}

	record ArrayAnnotationValueReference(
			String componentTypeName,
			List<AnnotationValueReference> values) implements AnnotationValueReference {
		@Serial
		private static final long serialVersionUID = 1L;

		ArrayAnnotationValueReference {
			values = List.copyOf( values );
		}
	}

	private static class RestoredTypeVariableDetails implements TypeVariableDetails {
		private final String identifier;
		private final ClassDetails declaringType;
		private List<TypeDetails> bounds;

		private RestoredTypeVariableDetails(String identifier, ClassDetails declaringType) {
			this.identifier = identifier;
			this.declaringType = declaringType;
		}

		private void setBounds(List<TypeDetails> bounds) {
			this.bounds = List.copyOf( bounds );
		}

		@Override
		public String getName() {
			return bounds == null || bounds.isEmpty() ? Object.class.getName() : bounds.get( 0 ).getName();
		}

		@Override
		public boolean isImplementor(Class<?> checkType) {
			if ( bounds != null && bounds.size() == 1 ) {
				return bounds.get( 0 ).isImplementor( checkType );
			}
			return checkType == Object.class;
		}

		@Override
		public TypeDetails resolveTypeVariable(TypeVariableDetails typeVariable) {
			return identifier.equals( typeVariable.getIdentifier() ) ? this : null;
		}

		@Override
		public String getIdentifier() {
			return identifier;
		}

		@Override
		public ClassDetails getDeclaringType() {
			return declaringType;
		}

		@Override
		public List<TypeDetails> getBounds() {
			return bounds;
		}
	}

	record FieldReference(int declaringClassId, String name) implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;
	}

	record MethodReference(int declaringClassId, String name, List<String> argumentTypeNames) implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		MethodReference {
			argumentTypeNames = List.copyOf( argumentTypeNames );
		}
	}

	record ConstructorReference(int declaringClassId, List<String> argumentTypeNames) implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		ConstructorReference {
			argumentTypeNames = List.copyOf( argumentTypeNames );
		}
	}

	record RecordComponentReference(int declaringClassId, String name) implements Serializable {
		@Serial
		private static final long serialVersionUID = 1L;
	}
}
