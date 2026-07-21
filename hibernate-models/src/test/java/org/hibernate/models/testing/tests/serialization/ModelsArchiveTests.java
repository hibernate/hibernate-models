/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.serialization;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.models.CompleteAnnotationDescriptor;
import org.hibernate.models.Creator;
import org.hibernate.models.internal.ModuleDetailsSupport;
import org.hibernate.models.dynamic.DynamicClassDetails;
import org.hibernate.models.serial.spi.ModelReference;
import org.hibernate.models.serial.spi.ModelsArchive;
import org.hibernate.models.serial.spi.ModelsArchiveWriter;
import org.hibernate.models.serial.spi.ModelsArchives;
import org.hibernate.models.serial.spi.RestoredModels;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ConstructorDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.RecordComponentDetails;
import org.hibernate.models.spi.RegistryPrimer;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.testing.util.SerializationHelper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;
import static org.hibernate.models.testing.TestHelper.createModelContext;

class ModelsArchiveTests {
	@Test
	void classReferencesRetainIdentity() {
		final ModelsContext sourceContext = createModelContext( ArchivedClass.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( ArchivedClass.class.getName() );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference firstReference = writer.reference( sourceClass );
		final ModelReference secondReference = writer.reference( sourceClass );

		assertThat( secondReference ).isEqualTo( firstReference );

		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );
		final RestoredModels restoredModels = archive.restore( SIMPLE_CLASS_LOADING, null );
		final ClassDetails firstRestored = (ClassDetails) restoredModels.resolve( firstReference );
		final ClassDetails secondRestored = (ClassDetails) restoredModels.resolve( secondReference );

		assertThat( firstRestored ).isSameAs( secondRestored );
		assertThat( firstRestored ).isNotSameAs( sourceClass );
		assertThat( firstRestored.getName() ).isEqualTo( ArchivedClass.class.getName() );
		assertThat( firstRestored.getFields() ).hasSize( 1 );
		assertThat( firstRestored.getMethods() ).hasSize( 1 );
		assertThat( restoredModels.getModelsContext().getClassDetailsRegistry()
				.findClassDetails( ArchivedClass.class.getName() ) ).isSameAs( firstRestored );
	}

	@Test
	void restoredClassReferencesResolveJavaClassThroughSuppliedClassLoading() {
		final ModelsContext sourceContext = createModelContext( ArchivedClass.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( ArchivedClass.class.getName() );
		final CountingClassLoading classLoading = new CountingClassLoading();

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference classReference = writer.reference( sourceClass );
		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );

		assertThat( classLoading.classForNameCalls ).isZero();

		final RestoredModels restoredModels = archive.restore( classLoading, null );
		final ClassDetails restoredClass = (ClassDetails) restoredModels.resolve( classReference );

		assertThat( restoredClass.toJavaClass( classLoading, restoredModels.getModelsContext() ) ).isSameAs( ArchivedClass.class );
		assertThat( classLoading.classForNameCalls ).isGreaterThan( 0 );
	}

	@Test
	void missingClassFailsDuringArchiveRestoreThroughSuppliedClassLoading() {
		final ModelsContext sourceContext = createModelContext( ArchivedClass.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( ArchivedClass.class.getName() );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		writer.reference( sourceClass );
		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );

		assertThatThrownBy( () -> archive.restore( new BlockingClassLoading( ArchivedClass.class.getName() ), null ) )
				.isInstanceOf( IllegalArgumentException.class )
				.hasMessageContaining( "blocked class" );
	}

	@Test
	void invalidReferenceIsRejected() {
		final ModelsArchive archive = ModelsArchives.createWriter( false ).finish();
		final RestoredModels restoredModels = archive.restore( SIMPLE_CLASS_LOADING, null );

		assertThatIllegalArgumentException()
				.isThrownBy( () -> restoredModels.resolve( new ModelReference( ModelReference.Kind.CLASS, 1 ) ) )
				.withMessageContaining( "out of range" );
	}

	@Test
	void memberReferencesResolveAgainstRestoredClassGraph() {
		final ModelsContext sourceContext = createModelContext( ArchivedClass.class, ArchivedRecord.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( ArchivedClass.class.getName() );
		final ClassDetails sourceRecord = sourceContext.getClassDetailsRegistry()
				.findClassDetails( ArchivedRecord.class.getName() );
		final FieldDetails sourceField = sourceClass.findFieldByName( "value" );
		final MethodDetails sourceMethod = sourceClass.getMethods().stream()
				.filter( method -> method.getName().equals( "getValue" ) )
				.findFirst()
				.orElseThrow();
		final RecordComponentDetails sourceRecordComponent = sourceRecord.findRecordComponentByName( "name" );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference fieldReference = writer.reference( sourceField );
		final ModelReference duplicateFieldReference = writer.reference( sourceField );
		final ModelReference methodReference = writer.reference( sourceMethod );
		final ModelReference recordComponentReference = writer.reference( sourceRecordComponent );

		assertThat( duplicateFieldReference ).isEqualTo( fieldReference );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final FieldDetails restoredField = (FieldDetails) restoredModels.resolve( fieldReference );
		final MethodDetails restoredMethod = (MethodDetails) restoredModels.resolve( methodReference );
		final RecordComponentDetails restoredRecordComponent = (RecordComponentDetails) restoredModels.resolve(
				recordComponentReference
		);

		assertThat( restoredField ).isNotSameAs( sourceField );
		assertThat( restoredField.getName() ).isEqualTo( "value" );
		assertThat( restoredField.getDeclaringType().getName() ).isEqualTo( ArchivedClass.class.getName() );
		assertThat( restoredMethod ).isNotSameAs( sourceMethod );
		assertThat( restoredMethod.getName() ).isEqualTo( "getValue" );
		assertThat( restoredRecordComponent ).isNotSameAs( sourceRecordComponent );
		assertThat( restoredRecordComponent.getName() ).isEqualTo( "name" );
		assertThat( restoredRecordComponent.getDeclaringType().getName() ).isEqualTo( ArchivedRecord.class.getName() );
	}

	@Test
	void typeReferencesResolveAgainstRestoredClassGraph() {
		final ModelsContext sourceContext = createModelContext( TypeFixture.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( TypeFixture.class.getName() );
		final FieldDetails listField = sourceClass.findFieldByName( "names" );
		final FieldDetails wildcardField = sourceClass.findFieldByName( "numbers" );
		final FieldDetails arrayField = sourceClass.findFieldByName( "matrix" );
		final FieldDetails primitiveField = sourceClass.findFieldByName( "count" );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference listTypeReference = writer.reference( listField.getType() );
		final ModelReference duplicateListTypeReference = writer.reference( listField.getType() );
		final ModelReference wildcardTypeReference = writer.reference( wildcardField.getType() );
		final ModelReference arrayTypeReference = writer.reference( arrayField.getType() );
		final ModelReference primitiveTypeReference = writer.reference( primitiveField.getType() );

		assertThat( duplicateListTypeReference ).isEqualTo( listTypeReference );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final TypeDetails restoredListType = (TypeDetails) restoredModels.resolve( listTypeReference );
		final TypeDetails restoredWildcardType = (TypeDetails) restoredModels.resolve( wildcardTypeReference );
		final TypeDetails restoredArrayType = (TypeDetails) restoredModels.resolve( arrayTypeReference );
		final TypeDetails restoredPrimitiveType = (TypeDetails) restoredModels.resolve( primitiveTypeReference );

		assertThat( restoredListType.getTypeKind() ).isEqualTo( TypeDetails.Kind.PARAMETERIZED_TYPE );
		assertThat( restoredListType.asParameterizedType().getRawClassDetails().getName() ).isEqualTo( List.class.getName() );
		assertThat( restoredListType.asParameterizedType().getArguments() ).hasSize( 1 );
		assertThat( restoredListType.asParameterizedType().getArguments().get( 0 ).getName() ).isEqualTo( String.class.getName() );
		assertThat( restoredWildcardType.asParameterizedType().getArguments().get( 1 ).getTypeKind() )
				.isEqualTo( TypeDetails.Kind.WILDCARD_TYPE );
		assertThat( restoredArrayType.getTypeKind() ).isEqualTo( TypeDetails.Kind.ARRAY );
		assertThat( restoredArrayType.asArrayType().getDimensions() ).isEqualTo( 2 );
		assertThat( restoredPrimitiveType.getTypeKind() ).isEqualTo( TypeDetails.Kind.PRIMITIVE );
		assertThat( restoredPrimitiveType.asPrimitiveType().toCode() ).isEqualTo( 'I' );
	}

	@Test
	void recursiveTypeVariableReferencesResolveToCanonicalTypeVariable() {
		final ModelsContext sourceContext = createModelContext( RecursiveType.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( RecursiveType.class.getName() );
		final TypeDetails sourceTypeVariable = sourceClass.getTypeParameters().get( 0 );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference typeVariableReference = writer.reference( sourceTypeVariable );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final TypeDetails restoredTypeVariable = (TypeDetails) restoredModels.resolve( typeVariableReference );
		final TypeDetails comparableBound = restoredTypeVariable.asTypeVariable().getBounds().get( 0 );
		final TypeDetails recursiveArgument = comparableBound.asParameterizedType().getArguments().get( 0 );

		assertThat( restoredTypeVariable.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
		assertThat( recursiveArgument.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE_REFERENCE );
		assertThat( recursiveArgument.asTypeVariableReference().getTarget() ).isSameAs( restoredTypeVariable );
	}

	@Test
	void directAnnotationUsagesAreRestoredFromArchive() {
		final ModelsContext sourceContext = createModelContext(
				AnnotatedType.class,
				ComplexAnnotation.class,
				MarkerAnnotation.class
		);
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( AnnotatedType.class.getName() );
		final FieldDetails sourceField = sourceClass.findFieldByName( "name" );
		final MethodDetails sourceMethod = sourceClass.getMethods().stream()
				.filter( method -> method.getName().equals( "getName" ) )
				.findFirst()
				.orElseThrow();

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference classReference = writer.reference( sourceClass );
		final ModelReference fieldReference = writer.reference( sourceField );
		final ModelReference methodReference = writer.reference( sourceMethod );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final ClassDetails restoredClass = (ClassDetails) restoredModels.resolve( classReference );
		final FieldDetails restoredField = (FieldDetails) restoredModels.resolve( fieldReference );
		final MethodDetails restoredMethod = (MethodDetails) restoredModels.resolve( methodReference );

		final ComplexAnnotation classUsage = restoredClass.getDirectAnnotationUsage( ComplexAnnotation.class );
		final ComplexAnnotation fieldUsage = restoredField.getDirectAnnotationUsage( ComplexAnnotation.class );
		final MarkerAnnotation methodUsage = restoredMethod.getDirectAnnotationUsage( MarkerAnnotation.class );

		assertThat( classUsage.name() ).isEqualTo( "type" );
		assertThat( classUsage.kind() ).isEqualTo( AnnotationKind.SECOND );
		assertThat( classUsage.javaType() ).isEqualTo( String.class );
		assertThat( classUsage.nested().value() ).isEqualTo( "nested-type" );
		assertThat( classUsage.tags() ).containsExactly( "a", "b" );
		assertThat( classUsage.counts() ).containsExactly( 1, 2 );
		assertThat( classUsage.nestedArray() ).extracting( MarkerAnnotation::value ).containsExactly( "x", "y" );
		assertThat( classUsage.defaulted() ).isEqualTo( "default-value" );
		assertThat( fieldUsage.name() ).isEqualTo( "field" );
		assertThat( methodUsage.value() ).isEqualTo( "method" );
	}

	@Test
	void mutableAnnotationDescriptorRequirementIsValidated() {
		final RegistryPrimer primer = (contributions, modelsContext) -> contributions.registerAnnotation(
				new CompleteAnnotationDescriptor<>(
						MutableMarker.class,
						MutableMarkerUsage.class,
						EnumSet.of( org.hibernate.models.spi.AnnotationTarget.Kind.CLASS ),
						false
				)
		);
		final ModelsContext sourceContext = createModelContext( primer, MutableAnnotatedType.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( MutableAnnotatedType.class.getName() );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference classReference = writer.reference( sourceClass );
		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );

		assertThatIllegalStateException()
				.isThrownBy( () -> archive.restore( SIMPLE_CLASS_LOADING, null ) )
				.withMessageContaining( "requires mutable contract" );

		final RestoredModels restoredModels = archive.restore( SIMPLE_CLASS_LOADING, primer );
		final ClassDetails restoredClass = (ClassDetails) restoredModels.resolve( classReference );
		final MutableMarker usage = restoredClass.getDirectAnnotationUsage( MutableMarker.class );

		assertThat( usage ).isInstanceOf( MutableMarkerUsage.class );
		assertThat( usage.value() ).isEqualTo( "mutable" );
	}

	@Test
	void repeatableAnnotationUsagesDeriveFromRestoredDirectUsages() {
		final ModelsContext sourceContext = createModelContext(
				RepeatableContainerOnly.class,
				RepeatableMixed.class
		);
		final ClassDetails sourceContainerOnly = sourceContext.getClassDetailsRegistry()
				.findClassDetails( RepeatableContainerOnly.class.getName() );
		final ClassDetails sourceMixed = sourceContext.getClassDetailsRegistry()
				.findClassDetails( RepeatableMixed.class.getName() );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference containerOnlyReference = writer.reference( sourceContainerOnly );
		final ModelReference mixedReference = writer.reference( sourceMixed );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final ClassDetails restoredContainerOnly = (ClassDetails) restoredModels.resolve( containerOnlyReference );
		final ClassDetails restoredMixed = (ClassDetails) restoredModels.resolve( mixedReference );

		assertThat( restoredContainerOnly.getDirectAnnotationUsage( RepeatableMarkers.class ) ).isNotNull();
		assertThat( restoredContainerOnly.getDirectAnnotationUsage( RepeatableMarker.class ) ).isNull();
		assertThat( restoredContainerOnly.getRepeatedAnnotationUsages( RepeatableMarker.class, restoredModels.getModelsContext() ) )
				.extracting( RepeatableMarker::value )
				.containsExactly( "a", "b" );

		assertThat( restoredMixed.getDirectAnnotationUsage( RepeatableMarkers.class ) ).isNotNull();
		assertThat( restoredMixed.getDirectAnnotationUsage( RepeatableMarker.class ).value() ).isEqualTo( "c" );
		assertThat( restoredMixed.getRepeatedAnnotationUsages( RepeatableMarker.class, restoredModels.getModelsContext() ) )
				.extracting( RepeatableMarker::value )
				.containsExactly( "c", "a", "b" );
	}

	@Test
	void inheritedAndMetaAnnotationLookupsWorkAfterRestore() {
		final ModelsContext sourceContext = createModelContext(
				InheritedAnnotatedBase.class,
				InheritedAnnotatedSubType.class,
				MetaAnnotatedType.class,
				InheritedMarker.class,
				MetaMarker.class,
				MetaStereotype.class
		);
		final ClassDetails sourceInheritedSubType = sourceContext.getClassDetailsRegistry()
				.findClassDetails( InheritedAnnotatedSubType.class.getName() );
		final ClassDetails sourceMetaAnnotatedType = sourceContext.getClassDetailsRegistry()
				.findClassDetails( MetaAnnotatedType.class.getName() );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference inheritedReference = writer.reference( sourceInheritedSubType );
		final ModelReference metaReference = writer.reference( sourceMetaAnnotatedType );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final ClassDetails restoredInheritedSubType = (ClassDetails) restoredModels.resolve( inheritedReference );
		final ClassDetails restoredMetaAnnotatedType = (ClassDetails) restoredModels.resolve( metaReference );

		assertThat( restoredInheritedSubType.getAnnotationUsage( InheritedMarker.class, restoredModels.getModelsContext() ).value() )
				.isEqualTo( "inherited" );
		assertThat( restoredMetaAnnotatedType.getDirectAnnotationUsage( MetaStereotype.class ) ).isNotNull();
		assertThat( restoredMetaAnnotatedType.locateAnnotationUsage( MetaMarker.class, restoredModels.getModelsContext() ).value() )
				.isEqualTo( "meta" );
	}

	@Test
	void constructorAnnotationUsagesAreRestoredFromArchive() {
		final ModelsContext sourceContext = createModelContext( ConstructorAnnotatedType.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( ConstructorAnnotatedType.class.getName() );
		final ConstructorDetails sourceConstructor = sourceClass.findConstructor(
				constructor -> constructor.getArgumentTypes().stream().map( ClassDetails::getName ).toList()
						.equals( List.of( String.class.getName() ) )
		);

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference constructorReference = writer.reference( sourceConstructor );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final ConstructorDetails restoredConstructor = (ConstructorDetails) restoredModels.resolve( constructorReference );
		final MarkerAnnotation usage = restoredConstructor.getDirectAnnotationUsage( MarkerAnnotation.class );

		assertThat( restoredConstructor ).isNotSameAs( sourceConstructor );
		assertThat( restoredConstructor.getArgumentTypes() ).extracting( ClassDetails::getName )
				.containsExactly( String.class.getName() );
		assertThat( usage.value() ).isEqualTo( "constructor" );
	}

	@Test
	void missingEnumAnnotationValueTypeFailsRestoration() {
		final ModelsContext sourceContext = createModelContext(
				AnnotatedType.class,
				ComplexAnnotation.class,
				MarkerAnnotation.class
		);
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( AnnotatedType.class.getName() );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		writer.reference( sourceClass );
		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );

		assertThatIllegalStateException()
				.isThrownBy( () -> archive.restore( new ClassLoading() {
					@Override
					public <T> Class<T> classForName(String name) {
						if ( name.equals( AnnotationKind.class.getName() ) ) {
							throw new IllegalArgumentException( "blocked enum type" );
						}
						return SIMPLE_CLASS_LOADING.classForName( name );
					}

					@Override
					public <T> Class<T> findClassForName(String name) {
						return SIMPLE_CLASS_LOADING.findClassForName( name );
					}

					@Override
					public URL locateResource(String resourceName) {
						return SIMPLE_CLASS_LOADING.locateResource( resourceName );
					}

					@Override
					public <S> Collection<S> loadJavaServices(Class<S> serviceType) {
						return SIMPLE_CLASS_LOADING.loadJavaServices( serviceType );
					}
				}, null ) )
				.withMessageContaining( AnnotationKind.class.getName() );
	}

	@Test
	void missingAnnotationClassFailsRestoration() {
		final ModelsContext sourceContext = createModelContext(
				AnnotatedType.class,
				ComplexAnnotation.class,
				MarkerAnnotation.class
		);
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( AnnotatedType.class.getName() );

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		writer.reference( sourceClass );
		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );

		assertThatThrownBy( () -> archive.restore( new BlockingClassLoading( ComplexAnnotation.class.getName() ), null ) )
				.isInstanceOf( IllegalArgumentException.class )
				.hasMessageContaining( "blocked class" );
	}

	@Test
	void moduleReferencesFailClearlyForNow() {
		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );

		assertThatIllegalArgumentException()
				.isThrownBy( () -> new ModelReference( ModelReference.Kind.MODULE, -1 ) )
				.withMessageContaining( "negative" );
		assertThat( ModelReference.Kind.MODULE ).isNotNull();
		assertThatThrownBy( () -> writer.reference( new ModuleDetailsSupport() {
			private final Map<Class<? extends Annotation>, ? extends Annotation> usageMap = new HashMap<>();

			@Override
			public String getModuleName() {
				return "test.module";
			}

			@Override
			public Map<Class<? extends Annotation>, ? extends Annotation> getUsageMap() {
				return usageMap;
			}
		} ) )
				.isInstanceOf( UnsupportedOperationException.class )
				.hasMessageContaining( "ModuleDetails archive entries are not implemented yet" );
	}

	@Test
	void modelAwareObjectStreamsReplaceAndResolveModelReferences() throws Exception {
		final ModelsContext sourceContext = createModelContext( ArchivedClass.class );
		final ClassDetails sourceClass = sourceContext.getClassDetailsRegistry()
				.findClassDetails( ArchivedClass.class.getName() );
		final FieldDetails sourceField = sourceClass.findFieldByName( "value" );
		final MethodDetails sourceMethod = sourceClass.getMethods().stream()
				.filter( method -> method.getName().equals( "getValue" ) )
				.findFirst()
				.orElseThrow();
		final TypeDetails sourceType = sourceField.getType();

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ByteArrayOutputStream payloadBytes = new ByteArrayOutputStream();
		try ( ObjectOutputStream objectOutputStream = ModelsArchives.createObjectOutputStream( payloadBytes, writer ) ) {
			objectOutputStream.writeObject( new Payload( sourceClass, sourceField, sourceMethod, sourceType ) );
		}
		final String payload = payloadBytes.toString( StandardCharsets.ISO_8859_1 );

		assertThat( payload ).contains( "ModelReferenceProxy" );
		assertThat( payload ).doesNotContain( "JdkClassDetails" );
		assertThat( payload ).doesNotContain( "JandexClassDetails" );
		assertThat( payload ).doesNotContain( "org.hibernate.models.bytebuddy.internal.ClassDetailsImpl" );

		final ModelsArchive archive = SerializationHelper.clone( writer.finish() );
		final RestoredModels restoredModels = archive.restore( SIMPLE_CLASS_LOADING, null );
		final Payload restoredPayload;
		try ( ObjectInputStream objectInputStream = ModelsArchives.createObjectInputStream(
				new ByteArrayInputStream( payloadBytes.toByteArray() ),
				restoredModels
		) ) {
			restoredPayload = (Payload) objectInputStream.readObject();
		}

		assertThat( restoredPayload.classDetails ).isInstanceOf( ClassDetails.class );
		assertThat( restoredPayload.classDetails ).isSameAs(
				restoredModels.getModelsContext().getClassDetailsRegistry()
						.findClassDetails( ArchivedClass.class.getName() )
		);
		assertThat( restoredPayload.fieldDetails ).isInstanceOf( FieldDetails.class );
		assertThat( ( (FieldDetails) restoredPayload.fieldDetails ).getDeclaringType() )
				.isSameAs( restoredPayload.classDetails );
		assertThat( restoredPayload.methodDetails ).isInstanceOf( MethodDetails.class );
		assertThat( ( (MethodDetails) restoredPayload.methodDetails ).getDeclaringType() )
				.isSameAs( restoredPayload.classDetails );
		assertThat( restoredPayload.typeDetails ).isInstanceOf( TypeDetails.class );
		assertThat( ( (TypeDetails) restoredPayload.typeDetails ).getName() ).isEqualTo( String.class.getName() );
	}

	@Test
	void dynamicClassDetailsAreRestoredFromArchive() {
		final ModelsContext sourceContext = createModelContext( String.class, Long.class );
		final ClassDetails stringClass = sourceContext.getClassDetailsRegistry().findClassDetails( String.class.getName() );
		final ClassDetails longClass = sourceContext.getClassDetailsRegistry().findClassDetails( Long.class.getName() );
		final DynamicClassDetails dynamicClass = (DynamicClassDetails) Creator.createDynamicClassDetails( "DynamicEntity", sourceContext );
		dynamicClass.applyAttribute(
				"id",
				longClass,
				false,
				false,
				sourceContext
		);
		dynamicClass.applyAttribute(
				"name",
				stringClass,
				false,
				false,
				sourceContext
		);

		final ModelsArchiveWriter writer = ModelsArchives.createWriter( false );
		final ModelReference classReference = writer.reference( dynamicClass );
		final ModelReference fieldReference = writer.reference( dynamicClass.findFieldByName( "name" ) );

		final RestoredModels restoredModels = SerializationHelper.clone( writer.finish() )
				.restore( SIMPLE_CLASS_LOADING, null );
		final ClassDetails restoredClass = (ClassDetails) restoredModels.resolve( classReference );
		final FieldDetails restoredField = (FieldDetails) restoredModels.resolve( fieldReference );

		assertThat( restoredClass.isRealClass() ).isFalse();
		assertThat( restoredClass.getName() ).isEqualTo( "DynamicEntity" );
		assertThat( restoredClass.getFields() ).extracting( FieldDetails::getName ).containsExactly( "id", "name" );
		assertThat( restoredField.getDeclaringType() ).isSameAs( restoredClass );
		assertThat( restoredField.getType().determineRawClass().getName() ).isEqualTo( String.class.getName() );
	}

	@SuppressWarnings("unused")
	private static class ArchivedClass {
		private String value;

		public String getValue() {
			return value;
		}
	}

	private record ArchivedRecord(String name) {
	}

	private record Payload(
			Object classDetails,
			Object fieldDetails,
			Object methodDetails,
			Object typeDetails) implements Serializable {
	}

	private static class CountingClassLoading implements ClassLoading {
		private int classForNameCalls;

		@Override
		public <T> Class<T> classForName(String name) {
			classForNameCalls++;
			return SIMPLE_CLASS_LOADING.classForName( name );
		}

		@Override
		public <T> Class<T> findClassForName(String name) {
			return SIMPLE_CLASS_LOADING.findClassForName( name );
		}

		@Override
		public URL locateResource(String resourceName) {
			return SIMPLE_CLASS_LOADING.locateResource( resourceName );
		}

		@Override
		public <S> Collection<S> loadJavaServices(Class<S> serviceType) {
			return SIMPLE_CLASS_LOADING.loadJavaServices( serviceType );
		}
	}

	private record BlockingClassLoading(String blockedClassName) implements ClassLoading {
		@Override
		public <T> Class<T> classForName(String name) {
			if ( name.equals( blockedClassName ) ) {
				throw new IllegalArgumentException( "blocked class: " + name );
			}
			return SIMPLE_CLASS_LOADING.classForName( name );
		}

		@Override
		public <T> Class<T> findClassForName(String name) {
			return SIMPLE_CLASS_LOADING.findClassForName( name );
		}

		@Override
		public URL locateResource(String resourceName) {
			return SIMPLE_CLASS_LOADING.locateResource( resourceName );
		}

		@Override
		public <S> Collection<S> loadJavaServices(Class<S> serviceType) {
			return SIMPLE_CLASS_LOADING.loadJavaServices( serviceType );
		}
	}

	@SuppressWarnings("unused")
	private static class TypeFixture {
		private List<String> names;
		private Map<String, ? extends Number> numbers;
		private String[][] matrix;
		private int count;

		public int getCount() {
			return count;
		}
	}

	private static class RecursiveType<T extends Comparable<T>> {
	}

	@InheritedMarker("inherited")
	private static class InheritedAnnotatedBase {
	}

	private static class InheritedAnnotatedSubType extends InheritedAnnotatedBase {
	}

	@MetaStereotype
	private static class MetaAnnotatedType {
	}

	@Inherited
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface InheritedMarker {
		String value();
	}

	@Target(ElementType.ANNOTATION_TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface MetaMarker {
		String value();
	}

	@MetaMarker("meta")
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface MetaStereotype {
	}

	@SuppressWarnings("unused")
	@ComplexAnnotation(
			name = "type",
			kind = AnnotationKind.SECOND,
			javaType = String.class,
			nested = @MarkerAnnotation("nested-type"),
			tags = { "a", "b" },
			counts = { 1, 2 },
			nestedArray = { @MarkerAnnotation("x"), @MarkerAnnotation("y") }
	)
	private static class AnnotatedType {
		@ComplexAnnotation(name = "field")
		private String name;

		@MarkerAnnotation("method")
		public String getName() {
			return name;
		}
	}

	private enum AnnotationKind {
		FIRST,
		SECOND
	}

	@Target({ ElementType.TYPE, ElementType.FIELD })
	@Retention(RetentionPolicy.RUNTIME)
	private @interface ComplexAnnotation {
		String name();

		AnnotationKind kind() default AnnotationKind.FIRST;

		Class<?> javaType() default Object.class;

		MarkerAnnotation nested() default @MarkerAnnotation("default-nested");

		String[] tags() default {};

		int[] counts() default {};

		MarkerAnnotation[] nestedArray() default {};

		String defaulted() default "default-value";
	}

	@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR })
	@Retention(RetentionPolicy.RUNTIME)
	private @interface MarkerAnnotation {
		String value();
	}

	@MutableMarker("mutable")
	private static class MutableAnnotatedType {
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface MutableMarker {
		String value();
	}

	public static class MutableMarkerUsage implements MutableMarker {
		private final String value;

		public MutableMarkerUsage(MutableMarker source, ModelsContext modelsContext) {
			this.value = source.value();
		}

		public MutableMarkerUsage(Map<String, Object> values, ModelsContext modelsContext) {
			this.value = (String) values.get( "value" );
		}

		@Override
		public String value() {
			return value;
		}

		@Override
		public Class<? extends Annotation> annotationType() {
			return MutableMarker.class;
		}
	}

	@RepeatableMarkers({
			@RepeatableMarker("a"),
			@RepeatableMarker("b")
	})
	private static class RepeatableContainerOnly {
	}

	@RepeatableMarkers({
			@RepeatableMarker("a"),
			@RepeatableMarker("b")
	})
	@RepeatableMarker("c")
	private static class RepeatableMixed {
	}

	private static class ConstructorAnnotatedType {
		@MarkerAnnotation("constructor")
		private ConstructorAnnotatedType(String name) {
		}
	}

	@Repeatable(RepeatableMarkers.class)
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface RepeatableMarker {
		String value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	private @interface RepeatableMarkers {
		RepeatableMarker[] value();
	}
}
