package org.hibernate.models.annotations;

import java.util.List;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.MutableInteger;
import org.hibernate.models.UnknownAnnotationAttributeException;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MutableAnnotationUsage;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.orm.JpaAnnotations;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.CheckConstraint;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.Table;

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

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( SimpleEntity.class.getName() );
		// NOTE : the 2 @NamedQuery refs get bundled into 1 @NamedQueries
		assertThat( classDetails.getAllAnnotationUsages() ).hasSize( 7 );

		assertThat( classDetails.findFieldByName( "id" ).getAllAnnotationUsages() ).hasSize( 2 );
		assertThat( classDetails.findFieldByName( "name" ).getAllAnnotationUsages() ).hasSize( 2 );
		assertThat( classDetails.findFieldByName( "name2" ).getAllAnnotationUsages() ).hasSize( 2 );
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

		assertThat( classDetails.hasAnnotationUsage( Entity.class ) ).isTrue();
		final AnnotationUsage<Entity> entityAnn = classDetails.getAnnotationUsage( Entity.class );
		assertThat( entityAnn.getString( "name" ) ).isEqualTo( "SimpleColumnEntity" );

		final AnnotationUsage<Column> columnAnn = classDetails.findFieldByName( "name" ).getAnnotationUsage( Column.class );
		assertThat( columnAnn.getString( "name" ) ).isEqualTo( "description" );
		assertThat( columnAnn.getString( "table" ) ).isEqualTo( "" );
		assertThat( columnAnn.getBoolean( "nullable" ) ).isFalse();
		assertThat( columnAnn.<Boolean>getAttributeValue( "unique" ) ).isTrue();
	}

	@Test
	void testCompositionsWithJandex() {
		compositionChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testCompositionsWithoutJandex() {
		compositionChecks( null );
	}

	private void compositionChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		assertThat( classDetails.hasAnnotationUsage( CustomMetaAnnotation.class ) ).isFalse();
		assertThat( classDetails.getAnnotationUsage( CustomMetaAnnotation.class ) ).isNull();
		assertThat( classDetails.locateAnnotationUsage( CustomMetaAnnotation.class ) ).isNotNull();

		assertThat( classDetails.getMetaAnnotated( CustomMetaAnnotation.class ) ).hasSize( 1 );
		assertThat( classDetails.getMetaAnnotated( CustomAnnotation.class ) ).isEmpty();
	}

	@Test
	void testDynamicAttributeCreation() {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( (Index) null, SimpleEntity.class );
		final AnnotationUsage<Column> usage = JpaAnnotations.COLUMN.createUsage( buildingContext );
		// check the attribute defaults
		assertThat( usage.getString( "name" ) ).isEqualTo( "" );
		assertThat( usage.getString( "table" ) ).isEqualTo( "" );
		assertThat( usage.getBoolean( "unique" ) ).isFalse();
		assertThat( usage.getBoolean( "nullable" ) ).isTrue();
		assertThat( usage.getBoolean( "insertable" ) ).isTrue();
		assertThat( usage.getBoolean( "updatable" ) ).isTrue();
		assertThat( usage.getString( "columnDefinition" ) ).isEqualTo( "" );
		assertThat( usage.getString( "options" ) ).isEqualTo( "" );
		assertThat( usage.getString( "comment" ) ).isEqualTo( "" );
		assertThat( usage.getInteger( "length" ) ).isEqualTo( 255 );
		assertThat( usage.getInteger( "precision" ) ).isEqualTo( 0 );
		assertThat( usage.getInteger( "scale" ) ).isEqualTo( 0 );
		assertThat( usage.getList( "check" ) ).isEmpty();
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

	@Test
	void testToAnnotationJandex() {
		toAnnotationChecks( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testToAnnotationWithoutJandex() {
		toAnnotationChecks( null );
	}

	private void toAnnotationChecks(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		{
			final AnnotationUsage<CustomAnnotation> annotationUsage = classDetails.getAnnotationUsage( CustomAnnotation.class );
			final CustomAnnotation annotation = annotationUsage.toAnnotation();
			assertThat( annotation ).isNotNull();
		}

		{
			final AnnotationUsage<Entity> annotationUsage = classDetails.getAnnotationUsage( Entity.class );
			final Entity annotation = annotationUsage.toAnnotation();
			assertThat( annotation.name() ).isEqualTo( "SimpleColumnEntity" );
		}

		{
			final AnnotationUsage<Column> annotationUsage = classDetails.findFieldByName( "name" ).getAnnotationUsage( Column.class );
			final Column annotation = annotationUsage.toAnnotation();
			assertThat( annotation.name() ).isEqualTo( "description" );
			assertThat( annotation.table() ).isEqualTo( "" );
			assertThat( annotation.nullable() ).isFalse();
			assertThat( annotation.unique() ).isTrue();
		}
	}

	@Test
	void testFromAnnotationsWithJandex() {
		testFromAnnotations( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testFromAnnotationsWithoutJandex() {
		testFromAnnotations( null );
	}

	void testFromAnnotations(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		final String query = classDetails.fromAnnotations( NamedQuery.class, (usage) -> {
			final String name = usage.getString( "name" );
			if ( "abc".equals( name ) ) {
				return usage.getString( "query" );
			}
			return null;
		} );
		assertThat( query ).isEqualTo( "select me" );

		final String query2 = classDetails.fromAnnotations( NamedQuery.class, (usage) -> {
			final String name = usage.getString( "name" );
			if ( "xyz".equals( name ) ) {
				return usage.getString( "query" );
			}
			return null;
		} );
		assertThat( query2 ).isEqualTo( "select you" );

		final AnnotationUsage<SecondaryTable> secondaryTable = classDetails.fromAnnotations( SecondaryTable.class, (usage) -> {
			final String name = usage.getString( "name" );
			if ( "another_table".equals( name ) ) {
				//noinspection unchecked
				return (AnnotationUsage<SecondaryTable>) usage;
			}
			return null;
		} );
		assertThat( query2 ).isEqualTo( "select you" );
	}

	@Test
	void testHasAnnotationWithJandex() {
		testHasAnnotation( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testHasAnnotationWithoutJandex() {
		testHasAnnotation( null );
	}

	void testHasAnnotation(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		// with #hasAnnotationUsage we only get true for the actual repeatable/container form used
		assertThat( classDetails.hasAnnotationUsage( NamedQuery.class ) ).isFalse();
		assertThat( classDetails.hasAnnotationUsage( NamedQueries.class ) ).isTrue();
		assertThat( classDetails.hasAnnotationUsage( SecondaryTable.class ) ).isTrue();
		assertThat( classDetails.hasAnnotationUsage( SecondaryTables.class ) ).isFalse();

		// with #hasRepeatableAnnotationUsage we get true regardless
		assertThat( classDetails.hasRepeatableAnnotationUsage( NamedQuery.class ) ).isTrue();
		assertThat( classDetails.hasRepeatableAnnotationUsage( NamedQueries.class ) ).isTrue();
		assertThat( classDetails.hasRepeatableAnnotationUsage( SecondaryTable.class ) ).isTrue();
		assertThat( classDetails.hasRepeatableAnnotationUsage( SecondaryTables.class ) ).isFalse();
	}

	@Test
	void testForEachAnnotationWithJandex() {
		testForEachAnnotation( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testForEachAnnotationWithoutJandex() {
		testForEachAnnotation( null );
	}

	void testForEachAnnotation(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		final MutableInteger counter = new MutableInteger();

		classDetails.forEachAnnotationUsage( Entity.class, entityAnnotationUsage -> counter.increment() );
		assertThat( counter.get() ).isEqualTo( 1 );

		counter.set( 0 );
		classDetails.forEachAnnotationUsage( SecondaryTable.class, entityAnnotationUsage -> counter.increment() );
		assertThat( counter.get() ).isEqualTo( 1 );

		counter.set( 0 );
		classDetails.forEachAnnotationUsage( NamedQuery.class, entityAnnotationUsage -> counter.increment() );
		assertThat( counter.get() ).isEqualTo( 2 );
	}

	@Test
	void testGetSingleUsageWithJandex() {
		testGetSingleUsage( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testGetSingleUsageWithoutJandex() {
		testGetSingleUsage( null );
	}

	void testGetSingleUsage(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		try {
			classDetails.getAnnotationUsage( NamedQuery.class );
			fail( "Expecting an AnnotationAccessException to be thrown" );
		}
		catch (AnnotationAccessException expected) {
			// this is expected
		}

		final AnnotationUsage<NamedQuery> singleAnnotationUsage = classDetails.getSingleAnnotationUsage( NamedQuery.class );
		assertThat( singleAnnotationUsage ).isNull();
	}

	@Test
	void testAnnotationTargetWithJandex() {
		testAnnotationTarget( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testAnnotationTargetWithoutJandex() {
		testAnnotationTarget( null );
	}

	void testAnnotationTarget(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		// type annotation usage
		final AnnotationUsage<Table> table = classDetails.getAnnotationUsage( Table.class );
		assertThat( table ).isNotNull();
		assertThat( table.getAnnotationDescriptor().getAllowableTargets() ).contains( classDetails.getKind() );
		// nested type annotation usage
		final AnnotationUsage<CheckConstraint> checkConstraint = table.<List<AnnotationUsage<CheckConstraint>>>getAttributeValue( "check" ).get( 0 );
		assertThat( checkConstraint.getAnnotationDescriptor().getAllowableTargets() ).isEmpty();
		assertThat( classDetails.getAnnotationUsage( CheckConstraint.class ) ).isNull();

		// field annotation usage
		final FieldDetails elementCollection = classDetails.findFieldByName( "elementCollection" );
		final AnnotationUsage<CollectionTable> collectionTable = elementCollection.getAnnotationUsage( CollectionTable.class );
		assertThat( collectionTable ).isNotNull();
		assertThat( collectionTable.getAnnotationDescriptor().getAllowableTargets() ).contains( elementCollection.getKind() );
		// nested field annotation usage
		final AnnotationUsage<JoinColumn> joinColumn = collectionTable.<List<AnnotationUsage<JoinColumn>>>getAttributeValue( "joinColumns" ).get( 0 );
		assertThat( joinColumn.getAnnotationDescriptor().getAllowableTargets() ).contains( elementCollection.getKind() );
		assertThat( elementCollection.getAnnotationUsage( JoinColumn.class ) ).isNull();

		// repeatable parent annotation usage
		final AnnotationUsage<NamedNativeQueries> namedNativeQueries = classDetails.getAnnotationUsage( NamedNativeQueries.class );
		assertThat( namedNativeQueries ).isNotNull();
		assertThat( namedNativeQueries.getAnnotationDescriptor().getAllowableTargets() ).contains( classDetails.getKind() );
		// nested repeated annotation usage
		final AnnotationUsage<NamedNativeQuery> namedNativeQuery = namedNativeQueries.<List<AnnotationUsage<NamedNativeQuery>>>getAttributeValue("value" ).get( 0 );
		assertThat( namedNativeQuery.getAnnotationDescriptor().getAllowableTargets() ).contains( classDetails.getKind() );
		assertThat( classDetails.getAnnotationUsage( NamedNativeQuery.class ) ).isSameAs( namedNativeQuery );

		// direct repeated annotation usage
		final List<AnnotationUsage<NamedQuery>> repeatedNamedQueries = classDetails.getRepeatedAnnotationUsages( NamedQuery.class );
		assertThat( repeatedNamedQueries ).hasSize( 2 )
				.allMatch( namedQuery -> namedQuery.getAnnotationDescriptor()
				.getAllowableTargets()
				.contains( classDetails.getKind() ) );
		assertThat( classDetails.getSingleAnnotationUsage( NamedQuery.class ) ).isNull();
		// we can still find a usage for the repeatable container
		final AnnotationUsage<NamedQueries> namedQueries = classDetails.getAnnotationUsage( NamedQueries.class );
		assertThat( namedQueries ).isNotNull();
		assertThat( namedQueries.getAnnotationDescriptor().getAllowableTargets() ).contains( classDetails.getKind() );
	}
}
