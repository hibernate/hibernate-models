package org.hibernate.models.classes;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.jdk.JdkClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;

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
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails rootClassDetails = classDetailsRegistry.resolveClassDetails(
				RootClass.class.getName(),
				JdkBuilders::buildClassDetailsStatic
		);
		assertThat( rootClassDetails ).isNotNull();

		final ClassDetails trunkClassDetails = classDetailsRegistry.resolveClassDetails(
				TrunkClass.class.getName(),
				(name) -> new JdkClassDetails(
						buildingContext.getClassLoading().classForName( name ),
						buildingContext
				)
		);
		assertThat( trunkClassDetails ).isNotNull();
	}

	@Test
	void testResolveClassDetailsVoid() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails voidClassDetails = classDetailsRegistry.resolveClassDetails(
				void.class.getName(),
				(name, ctx) -> {throw new IllegalStateException();}
		);
		assertThat( voidClassDetails ).isNotNull();

		final ClassDetails bigVoidClassDetails = classDetailsRegistry.resolveClassDetails(
				Void.class.getName(),
				(name) -> {throw new IllegalStateException();}
		);
		assertThat( bigVoidClassDetails ).isNotNull();
	}

	@Test
	void testResolveClassDetailsNull() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
				(Index) null,
				RootClass.class,
				TrunkClass.class,
				BranchClass.class,
				LeafClass.class
		);
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		try {
			final ClassDetails rootClassDetails = classDetailsRegistry.resolveClassDetails(
					null,
					(name, ctx) -> {
						throw new IllegalStateException();
					}
			);
			fail( "Expecting a failure" );
		}
		catch (IllegalArgumentException expected) {
		}

		try {
			final ClassDetails trunkClassDetails = classDetailsRegistry.resolveClassDetails(
					null,
					(name) -> {
						throw new IllegalStateException();
					}
			);
			fail( "Expecting a failure" );
		}
		catch (IllegalArgumentException expected) {
		}
	}

	@Test
	void testForEachClassDetails() {
		final SourceModelBuildingContextImpl buildingContext = SourceModelTestHelper.createBuildingContext(
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
