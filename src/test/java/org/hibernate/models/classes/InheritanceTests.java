package org.hibernate.models.classes;

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
	void basicRootTest() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
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
	void basicTrunkTest() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
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
	void basicLeafTest() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
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

	@ClassMarker
	@SubclassableMarker
	public static class RootClass {
		@MemberMarker
		private Integer value1;
		@Transient
		private Integer value2;
	}

	@ClassMarker
	public static class TrunkClass extends RootClass {
		@MemberMarker
		private Integer value3;
		@Transient
		private Integer value4;
	}

	@ClassMarker
	public static class BranchClass extends TrunkClass {
		@MemberMarker
		private Integer value5;
		@Transient
		private Integer value6;
	}

	@ClassMarker
	public static class LeafClass extends BranchClass {
		@MemberMarker
		private Integer value7;
		@Transient
		private Integer value8;
	}
}
