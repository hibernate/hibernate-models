package org.hibernate.models.classes;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * @author Steve Ebersole
 */
public class ClassRegistryTests {

	@Test
	void testResolveClassDetails() {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails rootClassDetails = classDetailsRegistry.resolveClassDetails( RootClass.class.getName() );
		assertThat( rootClassDetails ).isNotNull();

		final ClassDetails trunkClassDetails = classDetailsRegistry.resolveClassDetails( TrunkClass.class.getName() );
		assertThat( trunkClassDetails ).isNotNull();
	}

	@Test
	void testResolveClassDetailsVoid() {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails voidClassDetails = classDetailsRegistry.resolveClassDetails( void.class.getName() );
		assertThat( voidClassDetails ).isNotNull();

		final ClassDetails bigVoidClassDetails = classDetailsRegistry.resolveClassDetails( Void.class.getName() );
		assertThat( bigVoidClassDetails ).isNotNull();
	}

	@Test
	void testResolveClassDetailsNull() {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		try {
			classDetailsRegistry.resolveClassDetails( null );
			fail( "Expecting a failure" );
		}
		catch (IllegalArgumentException expected) {
		}
	}

	@Test
	void testForEachClassDetails() {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final Set<String> names = new HashSet<>();
		classDetailsRegistry.forEachClassDetails( (classDetails) -> {
			names.add( classDetails.getName() );
		} );
		assertThat( names ).contains( RootClass.class.getName() );
	}
}
