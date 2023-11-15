package org.hibernate.models.annotations;

import java.util.List;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.internal.MutableAnnotationUsage;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * Even though there are tons of annotations we need to look at, these descriptors
 * are created using a template.  Once we verify a few, we can logically conclude it
 * will work for all or at least the vast majority.
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageTests {
	@Test
	void testBasicUsageWithJandex() {
		basicUsageChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testBasicUsageWithoutJandex() {
		basicUsageChecks( null );
	}

	void basicUsageChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		assertThat( descriptor ).isNotNull();
		assertThat( metaDescriptor ).isNotNull();

		final AnnotationUsage<CustomMetaAnnotation> metaUsage = descriptor.getAnnotationUsage( metaDescriptor );
		assertThat( metaUsage ).isNotNull();
	}

	@Test
	void testUsageMutationWithJandex() {
		usageMutationChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testUsageMutationWithoutJandex() {
		usageMutationChecks( null );
	}

	private void usageMutationChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );
		final MutableAnnotationUsage<Entity> entityAnn = (MutableAnnotationUsage<Entity>) classDetails.getAnnotationUsage( Entity.class );
		entityAnn.setAttributeValue( "name", "SimpleEntity" );
	}

	@Test
	void testBaselineWithJandex() {
		baselineChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testBaselineWithoutJandex() {
		baselineChecks( null );
	}

	void baselineChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );
		final AnnotationUsage<CustomAnnotation> annotationUsage = classDetails.getAnnotationUsage( CustomAnnotation.class );
		assertThat( annotationUsage ).isNotNull();
		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		assertThat( descriptor ).isNotNull();
		final AnnotationUsage<CustomMetaAnnotation> customMetaAnnotationUsage = descriptor.getAnnotationUsage( CustomMetaAnnotation.class );
		assertThat( customMetaAnnotationUsage ).isNotNull();
		assertThat( customMetaAnnotationUsage.getString( "someValue" ) ).isEqualTo( "abc" );

		final AnnotationUsage<Entity> entityAnn = classDetails.getAnnotationUsage( Entity.class );
		assertThat( entityAnn.getString( "name" ) ).isEqualTo( "SimpleColumnEntity" );

		final AnnotationUsage<Cache> cacheAnn = classDetails.getAnnotationUsage( Cache.class );
		assertThat( cacheAnn.getEnum( "usage", CacheConcurrencyStrategy.class ) ).isEqualTo( CacheConcurrencyStrategy.READ_ONLY );

		final AnnotationUsage<Column> columnAnn = classDetails.findFieldByName( "name" ).getAnnotationUsage( Column.class );
		assertThat( columnAnn.getString( "name" ) ).isEqualTo( "description" );
		assertThat( columnAnn.getString( "table" ) ).isEqualTo( "" );
		assertThat( columnAnn.getBoolean( "nullable" ) ).isFalse();
		assertThat( columnAnn.<Boolean>getAttributeValue( "unique" ) ).isTrue();
	}

	@Test
	void testNamedAnnotationAccessWithJandex() {
		namedAnnotationAccessChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testNamedAnnotationAccessWithoutJandex() {
		namedAnnotationAccessChecks( null );
	}

	void namedAnnotationAccessChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClassDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		final List<AnnotationUsage<NamedQuery>> namedQueryAnns = entityClassDetails.getRepeatedAnnotationUsages( NamedQuery.class );
		assertThat( namedQueryAnns ).hasSize( 2 );

		final AnnotationUsage<NamedQuery> abcAnn = entityClassDetails.getNamedAnnotationUsage( NamedQuery.class, "abc" );
		assertThat( abcAnn ).isNotNull();
		assertThat( abcAnn.getString( "query" ) ).isEqualTo( "select me" );
	}

	@Test
	void testBadAttributeNamesWithJandex() {
		badAttributeNamesChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testBadAttributeNamesWithoutJandex() {
		badAttributeNamesChecks( null );
	}

	void badAttributeNamesChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails entityClassDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );
		final MutableAnnotationUsage<Entity> entityAnn = (MutableAnnotationUsage<Entity>) entityClassDetails.getAnnotationUsage( Entity.class );
		assertThat( entityAnn ).isNotNull();

		try {
			entityAnn.getAttributeValue( "doesNotExist" );
			fail( "Expecting an exception" );
		}
		catch (UnknownAnnotationAttributeException expected) {
		}

		try {
			entityAnn.setAttributeValue( "doesNotExist", "stuff" );
			fail( "Expecting an exception" );
		}
		catch (UnknownAnnotationAttributeException expected) {
		}
	}
}
