/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.classes;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Transient;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.buildModelContext;

/**
 * @author Steve Ebersole
 */
public class InheritanceTests {
	@Test
	void basicRootTest() {
		final ModelsContext modelsContext = buildModelContext(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails rootClassDetails = classDetailsRegistry.getClassDetails( RootClass.class.getName() );
		assertThat( rootClassDetails.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNotNull();
		assertThat( rootClassDetails.getAnnotationUsage( SubclassableMarker.class, modelsContext ) ).isNotNull();
		assertThat( rootClassDetails.getAnnotationUsage( Composable.class, modelsContext ) ).isNull();
		Assertions.assertThat( rootClassDetails.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNull();

		assertThat( rootClassDetails.getFields() ).hasSize( 2 );
		final FieldDetails value1 = rootClassDetails.findFieldByName( "value1" );
		assertThat( value1 ).isNotNull();
		assertThat( value1.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNotNull();
		assertThat( value1.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNull();
		assertThat( value1.getAnnotationUsage( Transient.class, modelsContext ) ).isNull();
		final FieldDetails value2 = rootClassDetails.findFieldByName( "value2" );
		assertThat( value2.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNull();
		assertThat( value2.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNull();
		assertThat( value2.getAnnotationUsage( Transient.class, modelsContext ) ).isNotNull();

		assertThat( rootClassDetails.getSuperClass() ).isNotNull();
		assertThat( rootClassDetails.getSuperClass().toJavaClass() ).isEqualTo( Object.class );
	}

	@Test
	void basicTrunkTest() {
		final ModelsContext modelsContext = buildModelContext(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails trunkClassDetails = classDetailsRegistry.getClassDetails( TrunkClass.class.getName() );
		assertThat( trunkClassDetails.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it here too
		assertThat( trunkClassDetails.getAnnotationUsage( SubclassableMarker.class, modelsContext ) ).isNotNull();
		assertThat( trunkClassDetails.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNull();

		assertThat( trunkClassDetails.getFields() ).hasSize( 2 );
		final FieldDetails value3 = trunkClassDetails.findFieldByName( "value3" );
		assertThat( value3 ).isNotNull();
		assertThat( value3.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNotNull();
		assertThat( value3.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNull();
		assertThat( value3.getAnnotationUsage( Transient.class, modelsContext ) ).isNull();

		final FieldDetails value4 = trunkClassDetails.findFieldByName( "value4" );
		assertThat( value4.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNull();
		assertThat( value4.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNull();
		assertThat( value4.getAnnotationUsage( Transient.class, modelsContext ) ).isNotNull();

		assertThat( trunkClassDetails.getSuperClass() ).isNotNull();
		assertThat( trunkClassDetails.getSuperClass().toJavaClass() ).isEqualTo( RootClass.class );
	}

	@Test
	void basicLeafTest() {
		final ModelsContext modelsContext = buildModelContext(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafClassDetails = classDetailsRegistry.getClassDetails( LeafClass.class.getName() );
		assertThat( leafClassDetails.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it here too
		assertThat( leafClassDetails.getAnnotationUsage( SubclassableMarker.class, modelsContext ) ).isNotNull();
		assertThat( leafClassDetails.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNull();

		assertThat( leafClassDetails.getAnnotationUsage( ClassMarker.class, modelsContext ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it even way down here too
		assertThat( leafClassDetails.getAnnotationUsage( SubclassableMarker.class, modelsContext ) ).isNotNull();
		assertThat( leafClassDetails.getAnnotationUsage( MemberMarker.class, modelsContext ) ).isNull();
	}

	@Test
	void testIsImplementor() {
		final ModelsContext modelsContext = buildModelContext(
				Intf.class,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails rootClassDetails = classDetailsRegistry.getClassDetails( RootClass.class.getName() );
		assertThat( rootClassDetails.isImplementor( Intf.class ) ).isFalse();
		assertThat( rootClassDetails.getSuperClass() ).isSameAs( ClassDetails.OBJECT_CLASS_DETAILS );
		assertThat( rootClassDetails.isInterface() ).isFalse();

		final ClassDetails branchClassDetails = classDetailsRegistry.getClassDetails( BranchClass.class.getName() );
		assertThat( branchClassDetails.isImplementor( Intf.class ) ).isTrue();
		assertThat( branchClassDetails.isInterface() ).isFalse();

		final ClassDetails leafClassDetails = classDetailsRegistry.getClassDetails( LeafClass.class.getName() );
		assertThat( leafClassDetails.isImplementor( Intf.class ) ).isTrue();
		assertThat( leafClassDetails.isInterface() ).isFalse();

		final ClassDetails interfaceDetails = classDetailsRegistry.getClassDetails( Intf.class.getName() );
		assertThat( interfaceDetails.isInterface() ).isTrue();
	}

	@Test
	void testForEachDirectSubType() {
		final ModelsContext modelsContext = buildModelContext(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final List<ClassDetails> subTypes = new ArrayList<>();
		classDetailsRegistry.forEachDirectSubtype( RootClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 1 );
		subTypes.clear();

		classDetailsRegistry.forEachDirectSubtype( TrunkClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 1 );
		subTypes.clear();

		classDetailsRegistry.forEachDirectSubtype( BranchClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 1 );
		subTypes.clear();

		classDetailsRegistry.forEachDirectSubtype( LeafClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 0 );
	}
}
