/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.bytebuddy.classes;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.bytebuddy.SourceModelTestHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Transient;
import net.bytebuddy.pool.TypePool;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class InheritanceTests {
	@Test
	void basicRootTestWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		basicRootTest( typePool );
	}

	@Test
	void basicRootTestWithoutJandex() {
		basicRootTest( null );
	}

	private void basicRootTest(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails rootClassDetails = classDetailsRegistry.getClassDetails( RootClass.class.getName() );
		assertThat( rootClassDetails.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNotNull();
		assertThat( rootClassDetails.getAnnotationUsage( SubclassableMarker.class, buildingContext ) ).isNotNull();
		assertThat( rootClassDetails.getAnnotationUsage( Composable.class, buildingContext ) ).isNull();
		Assertions.assertThat( rootClassDetails.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNull();

		assertThat( rootClassDetails.getFields() ).hasSize( 2 );
		final FieldDetails value1 = rootClassDetails.findFieldByName( "value1" );
		assertThat( value1 ).isNotNull();
		assertThat( value1.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNotNull();
		assertThat( value1.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNull();
		assertThat( value1.getAnnotationUsage( Transient.class, buildingContext ) ).isNull();
		final FieldDetails value2 = rootClassDetails.findFieldByName( "value2" );
		assertThat( value2.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNull();
		assertThat( value2.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNull();
		assertThat( value2.getAnnotationUsage( Transient.class, buildingContext ) ).isNotNull();

		assertThat( rootClassDetails.getSuperClass() ).isNotNull();
		assertThat( rootClassDetails.getSuperClass().toJavaClass() ).isEqualTo( Object.class );
	}

	@Test
	void basicTrunkTestWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		basicTrunkTest( typePool );
	}

	@Test
	void basicTrunkTestWithoutJandex() {
		basicTrunkTest( null );
	}

	private void basicTrunkTest(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails trunkClassDetails = classDetailsRegistry.getClassDetails( TrunkClass.class.getName() );
		assertThat( trunkClassDetails.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it here too
		assertThat( trunkClassDetails.getAnnotationUsage( SubclassableMarker.class, buildingContext ) ).isNotNull();
		assertThat( trunkClassDetails.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNull();

		assertThat( trunkClassDetails.getFields() ).hasSize( 2 );
		final FieldDetails value3 = trunkClassDetails.findFieldByName( "value3" );
		assertThat( value3 ).isNotNull();
		assertThat( value3.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNotNull();
		assertThat( value3.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNull();
		assertThat( value3.getAnnotationUsage( Transient.class, buildingContext ) ).isNull();

		final FieldDetails value4 = trunkClassDetails.findFieldByName( "value4" );
		assertThat( value4.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNull();
		assertThat( value4.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNull();
		assertThat( value4.getAnnotationUsage( Transient.class, buildingContext ) ).isNotNull();

		assertThat( trunkClassDetails.getSuperClass() ).isNotNull();
		assertThat( trunkClassDetails.getSuperClass().toJavaClass() ).isEqualTo( RootClass.class );
	}

	@Test
	void basicLeafTestWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		basicLeafTest( typePool );
	}

	@Test
	void basicLeafTestWithoutJandex() {
		basicLeafTest( null );
	}

	private void basicLeafTest(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails leafClassDetails = classDetailsRegistry.getClassDetails( LeafClass.class.getName() );
		assertThat( leafClassDetails.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it here too
		assertThat( leafClassDetails.getAnnotationUsage( SubclassableMarker.class, buildingContext ) ).isNotNull();
		assertThat( leafClassDetails.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNull();

		assertThat( leafClassDetails.getAnnotationUsage( ClassMarker.class, buildingContext ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it even way down here too
		assertThat( leafClassDetails.getAnnotationUsage( SubclassableMarker.class, buildingContext ) ).isNotNull();
		assertThat( leafClassDetails.getAnnotationUsage( MemberMarker.class, buildingContext ) ).isNull();
	}

	@Test
	void testIsImplementorWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool(
				Intf.class,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		testIsImplementor( typePool );
	}

	@Test
	void testIsImplementorWithoutJandex() {
		testIsImplementor( null );
	}

	private void testIsImplementor(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				Intf.class,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

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
	void testForEachDirectSubTypeWithJandex() {
		final TypePool typePool = SourceModelTestHelper.buildByteBuddyTypePool(
				Intf.class,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		testForEachDirectSubType( typePool );
	}

	@Test
	void testForEachDirectSubTypeWithoutJandex() {
		testForEachDirectSubType( null );
	}

	private void testForEachDirectSubType(TypePool typePool) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				typePool,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final List<ClassDetails> subTypes = new ArrayList<>();
		classDetailsRegistry.forEachDirectSubType( RootClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 1 );
		subTypes.clear();

		classDetailsRegistry.forEachDirectSubType( TrunkClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 1 );
		subTypes.clear();

		classDetailsRegistry.forEachDirectSubType( BranchClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 1 );
		subTypes.clear();

		classDetailsRegistry.forEachDirectSubType( LeafClass.class.getName(), subTypes::add );
		assertThat( subTypes ).hasSize( 0 );
	}

}
