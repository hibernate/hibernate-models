/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.models.serial.spi.ModelReference;
import org.hibernate.models.serial.spi.ModelsArchive;
import org.hibernate.models.serial.spi.ModelsArchiveWriter;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ConstructorDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ModuleDetails;
import org.hibernate.models.spi.MutableAnnotationDescriptor;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.TypeDetails;

/**
 * Standard identity-based archive collector.
 *
 * @author Steve Ebersole
 */
public class ModelsArchiveWriterImpl implements ModelsArchiveWriter {
	private final boolean trackImplementors;
	private final IdentityHashMap<ClassDetails, ModelReference> classReferences = new IdentityHashMap<>();
	private final Map<String, ModelReference> classReferencesByName = new HashMap<>();
	private final IdentityHashMap<FieldDetails, ModelReference> fieldReferences = new IdentityHashMap<>();
	private final IdentityHashMap<MethodDetails, ModelReference> methodReferences = new IdentityHashMap<>();
	private final IdentityHashMap<ConstructorDetails, ModelReference> constructorReferences = new IdentityHashMap<>();
	private final IdentityHashMap<RecordComponentDetails, ModelReference> recordComponentReferences = new IdentityHashMap<>();
	private final IdentityHashMap<TypeDetails, ModelReference> typeReferences = new IdentityHashMap<>();
	private final Map<String, ModelReference> inFlightTypeVariables = new HashMap<>();
	private final List<SerialClassDetails> classes = new ArrayList<>();
	private final List<ModelsArchiveImpl.TypeReference> types = new ArrayList<>();
	private final List<ModelsArchiveImpl.FieldReference> fields = new ArrayList<>();
	private final List<ModelsArchiveImpl.MethodReference> methods = new ArrayList<>();
	private final List<ModelsArchiveImpl.ConstructorReference> constructors = new ArrayList<>();
	private final List<ModelsArchiveImpl.RecordComponentReference> recordComponents = new ArrayList<>();
	private final List<ModelsArchiveImpl.AnnotationUsageReference> annotationUsages = new ArrayList<>();
	private boolean finished;

	public ModelsArchiveWriterImpl(boolean trackImplementors) {
		this.trackImplementors = trackImplementors;
	}

	@Override
	public ModelReference reference(ClassDetails details) {
		return referenceClassDetails( details, true );
	}

	private ModelReference referenceClassDetails(ClassDetails details, boolean captureAnnotationUsages) {
		checkActive();
		if ( details == null ) {
			throw new IllegalArgumentException( "Class details cannot be null" );
		}

		final ModelReference existing = classReferences.get( details );
		if ( existing != null ) {
			return existing;
		}
		final ModelReference existingByName = classReferencesByName.get( details.getName() );
		if ( existingByName != null ) {
			classReferences.put( details, existingByName );
			return existingByName;
		}

		final ModelReference reference = new ModelReference( ModelReference.Kind.CLASS, classes.size() );
		classReferences.put( details, reference );
		classReferencesByName.put( details.getName(), reference );
		classes.add( details.toSerialForm() );
		if ( captureAnnotationUsages ) {
			captureAnnotations( reference, details );
		}
		return reference;
	}

	@Override
	public ModelReference reference(TypeDetails details) {
		checkActive();
		if ( details == null ) {
			throw new IllegalArgumentException( "Type details cannot be null" );
		}

		final ModelReference existing = typeReferences.get( details );
		if ( existing != null ) {
			return existing;
		}

		final ModelReference reference = new ModelReference( ModelReference.Kind.TYPE, types.size() );
		typeReferences.put( details, reference );
		types.add( null );
		types.set( reference.id(), createTypeReference( details, reference ) );
		return reference;
	}

	@Override
	public ModelReference reference(FieldDetails details) {
		checkActive();
		if ( details == null ) {
			throw new IllegalArgumentException( "Field details cannot be null" );
		}

		final ModelReference existing = fieldReferences.get( details );
		if ( existing != null ) {
			return existing;
		}

		final ModelReference declaringTypeReference = reference( details.getDeclaringType() );
		final ModelReference reference = new ModelReference( ModelReference.Kind.FIELD, fields.size() );
		fieldReferences.put( details, reference );
		fields.add( new ModelsArchiveImpl.FieldReference( declaringTypeReference.id(), details.getName() ) );
		captureAnnotations( reference, details );
		return reference;
	}

	@Override
	public ModelReference reference(MethodDetails details) {
		checkActive();
		if ( details == null ) {
			throw new IllegalArgumentException( "Method details cannot be null" );
		}

		final ModelReference existing = methodReferences.get( details );
		if ( existing != null ) {
			return existing;
		}

		final ModelReference declaringTypeReference = reference( details.getDeclaringType() );
		final ModelReference reference = new ModelReference( ModelReference.Kind.METHOD, methods.size() );
		methodReferences.put( details, reference );
		methods.add( new ModelsArchiveImpl.MethodReference(
				declaringTypeReference.id(),
				details.getName(),
				details.getArgumentTypes().stream().map( ClassDetails::getName ).toList()
		) );
		captureAnnotations( reference, details );
		return reference;
	}

	@Override
	public ModelReference reference(RecordComponentDetails details) {
		checkActive();
		if ( details == null ) {
			throw new IllegalArgumentException( "Record component details cannot be null" );
		}

		final ModelReference existing = recordComponentReferences.get( details );
		if ( existing != null ) {
			return existing;
		}

		final ModelReference declaringTypeReference = reference( details.getDeclaringType() );
		final ModelReference reference = new ModelReference( ModelReference.Kind.RECORD_COMPONENT, recordComponents.size() );
		recordComponentReferences.put( details, reference );
		recordComponents.add( new ModelsArchiveImpl.RecordComponentReference( declaringTypeReference.id(), details.getName() ) );
		captureAnnotations( reference, details );
		return reference;
	}

	@Override
	public ModelReference reference(ConstructorDetails details) {
		checkActive();
		if ( details == null ) {
			throw new IllegalArgumentException( "Constructor details cannot be null" );
		}

		final ModelReference existing = constructorReferences.get( details );
		if ( existing != null ) {
			return existing;
		}

		final ModelReference declaringTypeReference = reference( details.getDeclaringType() );
		final ModelReference reference = new ModelReference( ModelReference.Kind.CONSTRUCTOR, constructors.size() );
		constructorReferences.put( details, reference );
		constructors.add( new ModelsArchiveImpl.ConstructorReference(
				declaringTypeReference.id(),
				details.getArgumentTypes().stream().map( ClassDetails::getName ).toList()
		) );
		captureAnnotations( reference, details );
		return reference;
	}

	@Override
	public ModelReference reference(ModuleDetails details) {
		checkActive();
		if ( details == null ) {
			throw new IllegalArgumentException( "Module details cannot be null" );
		}
		throw new UnsupportedOperationException(
				"ModuleDetails archive entries are not implemented yet; module annotations cannot be serialized"
		);
	}

	@Override
	public ModelsArchive finish() {
		checkActive();
		finished = true;
		return new ModelsArchiveImpl(
				trackImplementors,
				classes,
				types,
				fields,
				methods,
				constructors,
				recordComponents,
				annotationUsages
		);
	}

	private void captureAnnotations(ModelReference targetReference, AnnotationTarget target) {
		final ModelsContext modelsContext = resolveModelsContext( target );
		for ( Annotation usage : target.getDirectAnnotationUsages() ) {
			annotationUsages.add( createAnnotationUsageReference( targetReference, usage, modelsContext ) );
		}
	}

	private ModelsArchiveImpl.AnnotationUsageReference createAnnotationUsageReference(
			ModelReference targetReference,
			Annotation usage,
			ModelsContext modelsContext) {
		final LinkedHashMap<String, ModelsArchiveImpl.AnnotationValueReference> values = new LinkedHashMap<>();
		for ( Method attributeMethod : usage.annotationType().getDeclaredMethods() ) {
			values.put( attributeMethod.getName(), createAnnotationValueReference( extractAttributeValue( usage, attributeMethod ) ) );
		}
		return new ModelsArchiveImpl.AnnotationUsageReference(
				targetReference,
				usage.annotationType().getName(),
				determineMutableContractName( usage, modelsContext ),
				values
		);
	}

	private String determineMutableContractName(Annotation usage, ModelsContext modelsContext) {
		if ( modelsContext != null ) {
			final var descriptor = modelsContext.getAnnotationDescriptorRegistry().getDescriptor( usage.annotationType() );
			if ( descriptor instanceof MutableAnnotationDescriptor<?, ?> mutableDescriptor ) {
				return mutableDescriptor.getMutableAnnotationType().getName();
			}
		}

		final Class<? extends Annotation> annotationType = usage.annotationType();
		for ( Class<?> implementedInterface : usage.getClass().getInterfaces() ) {
			if ( annotationType != implementedInterface
					&& annotationType.isAssignableFrom( implementedInterface )
					&& Annotation.class.isAssignableFrom( implementedInterface ) ) {
				return implementedInterface.getName();
			}
		}
		return null;
	}

	private ModelsContext resolveModelsContext(AnnotationTarget target) {
		try {
			final Method getModelContext = target.getClass().getMethod( "getModelContext" );
			if ( !getModelContext.canAccess( target ) ) {
				getModelContext.setAccessible( true );
			}
			final Object result = getModelContext.invoke( target );
			return result instanceof ModelsContext modelsContext ? modelsContext : null;
		}
		catch (NoSuchMethodException e) {
			return null;
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException( "Could not resolve model context for annotation target: " + target, e );
		}
	}

	private Object extractAttributeValue(Annotation usage, Method attributeMethod) {
		try {
			if ( !attributeMethod.canAccess( usage ) ) {
				attributeMethod.setAccessible( true );
			}
			return attributeMethod.invoke( usage );
		}
		catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(
					"Could not extract annotation attribute `%s.%s`".formatted(
							usage.annotationType().getName(),
							attributeMethod.getName()
					),
					e
			);
		}
	}

	private ModelsArchiveImpl.AnnotationValueReference createAnnotationValueReference(Object value) {
		if ( value == null ) {
			return new ModelsArchiveImpl.NullAnnotationValueReference();
		}
		if ( value instanceof Annotation annotation ) {
			return createNestedAnnotationValueReference( annotation );
		}
		if ( value instanceof Class<?> javaClass ) {
			return new ModelsArchiveImpl.ClassAnnotationValueReference( referenceClassValue( javaClass ).id() );
		}
		if ( value instanceof Enum<?> enumValue ) {
			return new ModelsArchiveImpl.EnumAnnotationValueReference(
					enumValue.getDeclaringClass().getName(),
					enumValue.name()
			);
		}
		if ( value.getClass().isArray() ) {
			final ArrayList<ModelsArchiveImpl.AnnotationValueReference> values = new ArrayList<>();
			final int length = Array.getLength( value );
			for ( int i = 0; i < length; i++ ) {
				values.add( createAnnotationValueReference( Array.get( value, i ) ) );
			}
			return new ModelsArchiveImpl.ArrayAnnotationValueReference( value.getClass().getComponentType().getName(), values );
		}
		if ( value instanceof Boolean
				|| value instanceof Byte
				|| value instanceof Character
				|| value instanceof Double
				|| value instanceof Float
				|| value instanceof Integer
				|| value instanceof Long
				|| value instanceof Short
				|| value instanceof String ) {
			return new ModelsArchiveImpl.BasicAnnotationValueReference( value );
		}
		throw new UnsupportedOperationException( "Unsupported annotation attribute value: " + value );
	}

	private ModelsArchiveImpl.AnnotationValueReference createNestedAnnotationValueReference(Annotation annotation) {
		final LinkedHashMap<String, ModelsArchiveImpl.AnnotationValueReference> values = new LinkedHashMap<>();
		for ( Method attributeMethod : annotation.annotationType().getDeclaredMethods() ) {
			values.put( attributeMethod.getName(), createAnnotationValueReference( extractAttributeValue( annotation, attributeMethod ) ) );
		}
		return new ModelsArchiveImpl.NestedAnnotationValueReference( annotation.annotationType().getName(), values );
	}

	private ModelReference referenceClassValue(Class<?> javaClass) {
		return referenceClassDetails( new org.hibernate.models.internal.SimpleClassDetails( javaClass ), false );
	}

	private ModelsArchiveImpl.TypeReference createTypeReference(TypeDetails details, ModelReference currentReference) {
		return switch ( details.getTypeKind() ) {
			case CLASS, PRIMITIVE, VOID -> new ModelsArchiveImpl.ClassTypeReference(
					details.getTypeKind(),
					reference( details.determineRawClass() ).id()
			);
			case ARRAY -> new ModelsArchiveImpl.ArrayTypeReference(
					reference( details.asArrayType().getArrayClassDetails() ).id(),
					reference( details.asArrayType().getConstituentType() ).id()
			);
			case PARAMETERIZED_TYPE -> {
				final List<Integer> argumentIds = details.asParameterizedType()
						.getArguments()
						.stream()
						.map( type -> reference( type ).id() )
						.toList();
				yield new ModelsArchiveImpl.ParameterizedTypeReference(
						reference( details.asParameterizedType().getRawClassDetails() ).id(),
						argumentIds,
						createScopeReference( details.asParameterizedType().getOwner() )
				);
			}
			case TYPE_VARIABLE -> {
				final String identifier = details.asTypeVariable().getIdentifier();
				final ModelReference previousTypeVariableReference = inFlightTypeVariables.put( identifier, currentReference );
				final List<Integer> boundIds = details.asTypeVariable()
						.getBounds()
						.stream()
						.map( type -> reference( type ).id() )
						.toList();
				if ( previousTypeVariableReference == null ) {
					inFlightTypeVariables.remove( identifier );
				}
				else {
					inFlightTypeVariables.put( identifier, previousTypeVariableReference );
				}
				yield new ModelsArchiveImpl.TypeVariableReference(
						identifier,
						reference( details.asTypeVariable().getDeclaringType() ).id(),
						boundIds
				);
			}
			case TYPE_VARIABLE_REFERENCE -> new ModelsArchiveImpl.TypeVariableTargetReference(
					details.asTypeVariableReference().getIdentifier(),
					resolveTypeVariableReferenceTarget( details ).id()
			);
			case WILDCARD_TYPE -> new ModelsArchiveImpl.WildcardTypeReference(
					details.asWildcardType().getBound() == null ? -1 : reference( details.asWildcardType().getBound() ).id(),
					details.asWildcardType().isExtends()
			);
		};
	}

	private ModelReference resolveTypeVariableReferenceTarget(TypeDetails details) {
		try {
			return reference( details.asTypeVariableReference().getTarget() );
		}
		catch (IllegalStateException e) {
			final ModelReference inFlightReference = inFlightTypeVariables.get( details.asTypeVariableReference().getIdentifier() );
			if ( inFlightReference != null ) {
				return inFlightReference;
			}
			throw e;
		}
	}

	private ModelsArchiveImpl.ScopeReference createScopeReference(org.hibernate.models.spi.TypeVariableScope owner) {
		if ( owner == null ) {
			return null;
		}
		if ( owner instanceof ClassDetails classDetails ) {
			return new ModelsArchiveImpl.ScopeReference( ModelReference.Kind.CLASS, reference( classDetails ).id() );
		}
		if ( owner instanceof TypeDetails typeDetails ) {
			return new ModelsArchiveImpl.ScopeReference( ModelReference.Kind.TYPE, reference( typeDetails ).id() );
		}
		throw new UnsupportedOperationException( "Unsupported type-variable scope in archive: " + owner );
	}

	private void checkActive() {
		if ( finished ) {
			throw new IllegalStateException( "Models archive writer is already finished" );
		}
	}

}
