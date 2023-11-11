package org.hibernate.models;

import org.hibernate.models.internal.util.StringHelper;
import org.hibernate.models.orm.HibernateAnnotations;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class PackageTests {
	private static final String PACKAGE_NAME = StringHelper.qualifier( PackageTests.class.getName() ) + ".pkg";

	@Test
	void testExactReference() {
		final SourceModelBuildingContext buildingContext = createBuildingContext();
		final String packageInfoName = PACKAGE_NAME + ".package-info";
		final ClassDetails classDetails = buildingContext
				.getClassDetailsRegistry()
				.resolveClassDetails( packageInfoName );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getClassName() ).endsWith( "package-info" );
		assertThat( classDetails.getAnnotationUsage( HibernateAnnotations.SOFT_DELETE ) ).isNotNull();
	}

	@Test
	void testPackageReference() {
		final SourceModelBuildingContext buildingContext = createBuildingContext();
		final ClassDetails classDetails = buildingContext
				.getClassDetailsRegistry()
				.resolveClassDetails( PACKAGE_NAME );
		assertThat( classDetails ).isNotNull();
		assertThat( classDetails.getClassName() ).endsWith( "package-info" );
		assertThat( classDetails.getAnnotationUsage( HibernateAnnotations.SOFT_DELETE ) ).isNotNull();
	}
}
