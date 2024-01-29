package org.hibernate.models.classes;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.Transient;
import org.assertj.core.api.Assertions;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class InheritanceTests {
	@Test
	void basicRootTestWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		basicRootTest( index );
	}

	@Test
	void basicRootTestWithoutJandex() {
		basicRootTest( null );
	}

	private void basicRootTest(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails rootClassDetails = classDetailsRegistry.getClassDetails( RootClass.class.getName() );
		assertThat( rootClassDetails.getAnnotationUsage( ClassMarker.class ) ).isNotNull();
		assertThat( rootClassDetails.getAnnotationUsage( SubclassableMarker.class ) ).isNotNull();
		assertThat( rootClassDetails.getAnnotationUsage( Composable.class ) ).isNull();
		Assertions.assertThat( rootClassDetails.getAnnotationUsage( MemberMarker.class ) ).isNull();

		assertThat( rootClassDetails.getFields() ).hasSize( 2 );
		final FieldDetails value1 = rootClassDetails.findFieldByName( "value1" );
		assertThat( value1 ).isNotNull();
		assertThat( value1.getAnnotationUsage( MemberMarker.class ) ).isNotNull();
		assertThat( value1.getAnnotationUsage( ClassMarker.class ) ).isNull();
		assertThat( value1.getAnnotationUsage( Transient.class ) ).isNull();
		final FieldDetails value2 = rootClassDetails.findFieldByName( "value2" );
		assertThat( value2.getAnnotationUsage( MemberMarker.class ) ).isNull();
		assertThat( value2.getAnnotationUsage( ClassMarker.class ) ).isNull();
		assertThat( value2.getAnnotationUsage( Transient.class ) ).isNotNull();

		assertThat( rootClassDetails.getSuperType() ).isNotNull();
		assertThat( rootClassDetails.getSuperType().toJavaClass() ).isEqualTo( Object.class );
	}

	@Test
	void basicTrunkTestWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		basicTrunkTest( index );
	}

	@Test
	void basicTrunkTestWithoutJandex() {
		basicTrunkTest( null );
	}

	private void basicTrunkTest(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails trunkClassDetails = classDetailsRegistry.getClassDetails( TrunkClass.class.getName() );
		assertThat( trunkClassDetails.getAnnotationUsage( ClassMarker.class ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it here too
		assertThat( trunkClassDetails.getAnnotationUsage( SubclassableMarker.class ) ).isNotNull();
		assertThat( trunkClassDetails.getAnnotationUsage( MemberMarker.class ) ).isNull();

		assertThat( trunkClassDetails.getFields() ).hasSize( 2 );
		final FieldDetails value3 = trunkClassDetails.findFieldByName( "value3" );
		assertThat( value3 ).isNotNull();
		assertThat( value3.getAnnotationUsage( MemberMarker.class ) ).isNotNull();
		assertThat( value3.getAnnotationUsage( ClassMarker.class ) ).isNull();
		assertThat( value3.getAnnotationUsage( Transient.class ) ).isNull();

		final FieldDetails value4 = trunkClassDetails.findFieldByName( "value4" );
		assertThat( value4.getAnnotationUsage( MemberMarker.class ) ).isNull();
		assertThat( value4.getAnnotationUsage( ClassMarker.class ) ).isNull();
		assertThat( value4.getAnnotationUsage( Transient.class ) ).isNotNull();

		assertThat( trunkClassDetails.getSuperType() ).isNotNull();
		assertThat( trunkClassDetails.getSuperType().toJavaClass() ).isEqualTo( RootClass.class );
	}

	@Test
	void basicLeafTestWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		basicLeafTest( index );
	}

	@Test
	void basicLeafTestWithoutJandex() {
		basicLeafTest( null );
	}

	private void basicLeafTest(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails leafClassDetails = classDetailsRegistry.getClassDetails( LeafClass.class.getName() );
		assertThat( leafClassDetails.getAnnotationUsage( ClassMarker.class ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it here too
		assertThat( leafClassDetails.getAnnotationUsage( SubclassableMarker.class ) ).isNotNull();
		assertThat( leafClassDetails.getAnnotationUsage( MemberMarker.class ) ).isNull();

		assertThat( leafClassDetails.getAnnotationUsage( ClassMarker.class ) ).isNotNull();
		// NOTE : SubclassableMarker is @Inherited, so we should see it even way down here too
		assertThat( leafClassDetails.getAnnotationUsage( SubclassableMarker.class ) ).isNotNull();
		assertThat( leafClassDetails.getAnnotationUsage( MemberMarker.class ) ).isNull();
	}

	@Test
	void testIsImplementorWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				Intf.class,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		testIsImplementor( index );
	}

	@Test
	void testIsImplementorWithoutJandex() {
		testIsImplementor( null );
	}

	private void testIsImplementor(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails rootClassDetails = classDetailsRegistry.getClassDetails( RootClass.class.getName() );
		assertThat( rootClassDetails.isImplementor( Intf.class ) ).isFalse();
		assertThat( rootClassDetails.getSuperType() ).isSameAs( ClassDetails.OBJECT_CLASS_DETAILS );

		final ClassDetails branchClassDetails = classDetailsRegistry.getClassDetails( BranchClass.class.getName() );
		assertThat( branchClassDetails.isImplementor( Intf.class ) ).isTrue();

		final ClassDetails leafClassDetails = classDetailsRegistry.getClassDetails( LeafClass.class.getName() );
		assertThat( leafClassDetails.isImplementor( Intf.class ) ).isTrue();
	}

	@Test
	void testForEachDirectSubTypeWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				Intf.class,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		testForEachDirectSubType( index );
	}

	@Test
	void testForEachDirectSubTypeWithoutJandex() {
		testForEachDirectSubType( null );
	}

	private void testForEachDirectSubType(Index index) {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
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
