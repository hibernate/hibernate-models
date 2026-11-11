/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.hibernate.models.Creator;
import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.ArrayTypeDescriptor;
import org.hibernate.models.internal.StringTypeDescriptor;
import org.hibernate.models.rendering.internal.SimpleRenderer;
import org.hibernate.models.rendering.spi.AbstractRenderingTarget;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ModelsConfiguration;
import org.hibernate.models.internal.MutableClassDetailsRegistry;
import org.hibernate.models.spi.TypeDetailsHelper;
import org.hibernate.models.internal.AbstractClassDetailsRegistry;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.hibernate.models.spi.StandardTypeDetails.OBJECT_TYPE_DETAILS;
import static org.hibernate.models.testing.TestHelper.buildModelContext;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/// Regression coverage for nullable public contracts and their nonnull alternatives.
///
/// @author Steve Ebersole
class NullabilityContractTests {
	@Test
	void voidLookupDependsOnRegistration() {
		final var context = createModelContext();
		final var registry = context.getClassDetailsRegistry();
		assertThat( registry.getClassDetails( "void" ) ).isNotNull();

		registry.as( AbstractClassDetailsRegistry.class ).classDetailsMap().remove( "void" );
		assertThat( registry.getClassDetails( "void" ) ).isNull();
		assertThat( registry.as( MutableClassDetailsRegistry.class ).resolveClassDetails( "void", name -> {
			fail( "The creator must not be called for void" );
			return ClassDetails.VOID_CLASS_DETAILS;
		} ) ).isNull();

		assertThat( registry.findClassDetails( "no.such.Model" ) ).isNull();
		assertThatThrownBy( () -> registry.getClassDetails( "no.such.Model" ) )
				.isInstanceOf( UnknownClassException.class );
	}

	@Test
	void nullExclusionsRetainAllImplementors() {
		final var context = buildModelContext( PlainMember.class );
		final var registry = context.getClassDetailsRegistry();
		final String name = PlainMember.class.getName();
		final var details = registry.resolveClassDetails( name );

		assertThat( registry.collectImplementors( name, true, null ) )
				.containsExactly( details )
				.isEqualTo( registry.collectImplementors( name, true ) );
		assertThat( registry.collectImplementors( name, true, candidate -> true ) ).isEmpty();
	}

	@Test
	void modelsWithoutMetadataPreserveNullResults() {
		final var context = createModelContext();
		final var dynamic = Creator.createDynamicClassDetails( "DynamicModel", context );
		assertThat( dynamic.getClassName() ).isNull();
		assertThat( dynamic.getSuperClass() ).isNull();
		assertThat( dynamic.getGenericSuperType() ).isNull();
		assertThat( dynamic.getImplementedInterfaces() ).isNull();
		assertThat( dynamic.getFields() ).isEmpty();

		assertThat( ClassDetails.OBJECT_CLASS_DETAILS.getRepeatedAnnotationUsages( Deprecated.class, context ) ).isNull();
		final var missingPackage = context.getClassDetailsRegistry().resolvePackageDetails( "no.such.package" );
		assertThat( missingPackage.isRealClass() ).isFalse();
		final Annotation[] usages = missingPackage.getRepeatedAnnotationUsages( Deprecated.class, context );
		assertThat( usages ).isEmpty();
	}

	@Test
	void ordinaryMethodsHaveNoAttributeType() {
		final var context = buildModelContext( PlainMember.class );
		final var details = context.getClassDetailsRegistry().resolveClassDetails( PlainMember.class.getName() );
		final var method = details.getMethods().stream()
				.filter( candidate -> candidate.getName().equals( "run" ) )
				.findFirst().orElseThrow();
		assertThat( method.getType() ).isNull();
		assertThat( method.resolveAttributeName() ).isNull();
		assertThat( method.getReturnType() ).isNotNull();
	}

	@Test
	void typeHelpersAcceptMissingTypes() {
		assertThat( TypeDetailsHelper.extractElementType( null ) ).isNull();
		assertThat( TypeDetailsHelper.extractCollectionElementType( null ) ).isSameAs( OBJECT_TYPE_DETAILS );
		assertThat( TypeDetailsHelper.extractMapKeyType( null ) ).isSameAs( OBJECT_TYPE_DETAILS );
		assertThat( TypeDetailsHelper.extractMapValueType( null ) ).isSameAs( OBJECT_TYPE_DETAILS );
		assertThat( TypeDetailsHelper.resolveSuperType( null, Map.class ) ).isNull();
	}

	@Test
	void optionalConfigurationCanBeCleared() {
		final var configuration = new ModelsConfiguration();
		assertThat( configuration.getRegistryPrimer() ).isNull();
		assertThat( configuration.getExplicitContextProvider() ).isNull();
		configuration.setRegistryPrimer( (contributions, context) -> {} );
		configuration.setExplicitContextProvider( (classLoading, primer, values) -> null );
		assertThat( configuration.bootstrap() ).isNotNull();
		configuration.setRegistryPrimer( null ).setExplicitContextProvider( null );
		assertThat( configuration.getRegistryPrimer() ).isNull();
		assertThat( configuration.getExplicitContextProvider() ).isNull();
		assertThat( configuration.setConfigValue( "optional", null ) ).isNull();
		assertThat( configuration.getConfigValues() ).containsEntry( "optional", null );
	}

	@Test
	void scalarUnwrappingPreservesIncompleteValues() {
		assertThat( StringTypeDescriptor.STRING_TYPE_DESCRIPTOR.unwrap( null ) ).isNull();
		final var arrayDescriptor = new ArrayTypeDescriptor<>( StringTypeDescriptor.STRING_TYPE_DESCRIPTOR );
		assertThat( arrayDescriptor.unwrap( new String[0] ) ).isEqualTo( new String[0] );
		assertThatThrownBy( () -> arrayDescriptor.unwrap( null ) ).isInstanceOf( NullPointerException.class );
	}

	@Test
	void scalarRenderingPreservesIncompleteValues() {
		final var output = new StringBuilder();
		final var target = new AbstractRenderingTarget() {
			@Override
			protected void write(String chars) {
				output.append( chars );
			}
		};
		final var renderer = new SimpleRenderer( target );
		final var context = createModelContext();
		StringTypeDescriptor.STRING_TYPE_DESCRIPTOR.render( "value", null, target, renderer, context );
		StringTypeDescriptor.STRING_TYPE_DESCRIPTOR.render( null, target, renderer, context );
		assertThat( output.toString() ).isEqualTo( "value = \"null\"\n\"null\"\n" );
	}

	static class PlainMember {
		public void run() {
		}
	}
}
