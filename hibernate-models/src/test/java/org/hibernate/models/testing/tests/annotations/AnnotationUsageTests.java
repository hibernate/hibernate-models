/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations;

import java.lang.reflect.Proxy;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.testing.annotations.CustomAnnotation;
import org.hibernate.models.testing.annotations.CustomMetaAnnotation;
import org.hibernate.models.testing.domain.SimpleEntity;
import org.hibernate.models.testing.orm.EntityAnnotation;
import org.hibernate.models.testing.orm.JpaAnnotations;
import org.hibernate.models.testing.orm.NamedQueryAnnotation;
import org.hibernate.models.testing.orm.SecondaryTableAnnotation;
import org.hibernate.models.testing.orm.TableAnnotation;
import org.hibernate.models.testing.util.MutableInteger;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * Even though there are tons of annotations we need to look at, these descriptors
 * are created using a template.  Once we verify a few, we can logically conclude it
 * will work for all or at least the vast majority.
 *
 * @author Steve Ebersole
 */
public class AnnotationUsageTests {
	@Test
	void testBasicUsage() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final AnnotationDescriptorRegistry descriptorRegistry = modelsContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final AnnotationDescriptor<CustomMetaAnnotation> metaDescriptor = descriptorRegistry.getDescriptor( CustomMetaAnnotation.class );
		assertThat( descriptor ).isNotNull();
		assertThat( metaDescriptor ).isNotNull();

		final CustomMetaAnnotation metaUsage = descriptor.getAnnotationUsage( metaDescriptor, modelsContext );
		assertThat( metaUsage ).isNotNull();
		assertThat( Proxy.isProxyClass( metaUsage.getClass() ) ).isTrue();

		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry().getClassDetails( SimpleEntity.class.getName() );
		// NOTE : the 2 @NamedQuery refs get bundled into 1 @NamedQueries
		assertThat( classDetails.getDirectAnnotationUsages() ).hasSize( 7 );

		assertThat( classDetails.findFieldByName( "id" ).getDirectAnnotationUsages() ).hasSize( 2 );
		assertThat( classDetails.findFieldByName( "name" ).getDirectAnnotationUsages() ).hasSize( 2 );
		assertThat( classDetails.findFieldByName( "name2" ).getDirectAnnotationUsages() ).hasSize( 2 );

		final Entity entityUsage = classDetails.getDirectAnnotationUsage( Entity.class );
		assertThat( entityUsage ).isInstanceOf( EntityAnnotation.class );

		final Table tableUsage = classDetails.getDirectAnnotationUsage( Table.class );
		assertThat( tableUsage ).isInstanceOf( TableAnnotation.class );

		final SecondaryTable secondaryTableUsage = classDetails.getDirectAnnotationUsage( SecondaryTable.class );
		assertThat( secondaryTableUsage ).isInstanceOf( SecondaryTableAnnotation .class );

		assertThat( classDetails.getDirectAnnotationUsage( NamedQuery.class ) ).isNull();
		final NamedQuery[] namedQueryUsages = classDetails.getRepeatedAnnotationUsages( NamedQuery.class, modelsContext );
		assertThat( namedQueryUsages ).hasSize( 2 );
		assertThat( namedQueryUsages[0] ).isInstanceOf( NamedQueryAnnotation .class );
		assertThat( namedQueryUsages[1] ).isInstanceOf( NamedQueryAnnotation.class );
	}

	@Test
	void testUsageMutation() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );
		final EntityAnnotation entityAnn = (EntityAnnotation) classDetails.getAnnotationUsage( Entity.class, modelsContext );
		entityAnn.name( "SimpleEntity" );
		assertThat( entityAnn.name() ).isEqualTo( "SimpleEntity" );
	}

	@Test
	void testBaseline() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final AnnotationDescriptorRegistry descriptorRegistry = modelsContext.getAnnotationDescriptorRegistry();
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );
		final CustomAnnotation annotationUsage = classDetails.getAnnotationUsage( CustomAnnotation.class, modelsContext );
		assertThat( annotationUsage ).isNotNull();
		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		assertThat( descriptor ).isNotNull();
		final CustomMetaAnnotation customMetaAnnotationUsage = descriptor.getAnnotationUsage( CustomMetaAnnotation.class, modelsContext );
		assertThat( customMetaAnnotationUsage ).isNotNull();
		assertThat( customMetaAnnotationUsage.someValue() ).isEqualTo( "abc" );

		assertThat( classDetails.hasDirectAnnotationUsage( Entity.class ) ).isTrue();
		final Entity entityAnn = classDetails.getAnnotationUsage( Entity.class, modelsContext );
		assertThat( entityAnn.name() ).isEqualTo( "SimpleColumnEntity" );

		final Column columnAnn = classDetails.findFieldByName( "name" ).getAnnotationUsage( Column.class, modelsContext );
		assertThat( columnAnn.name() ).isEqualTo( "description" );
		assertThat( columnAnn.table() ).isEqualTo( "" );
		assertThat( columnAnn.nullable() ).isFalse();
		assertThat( columnAnn.unique() ).isTrue();
	}

	@Test
	void testCompositions() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		assertThat( classDetails.hasDirectAnnotationUsage( CustomMetaAnnotation.class ) ).isFalse();
		assertThat( classDetails.getAnnotationUsage( CustomMetaAnnotation.class, modelsContext ) ).isNull();
		assertThat( classDetails.locateAnnotationUsage( CustomMetaAnnotation.class, modelsContext ) ).isNotNull();

		assertThat( classDetails.getMetaAnnotated( CustomMetaAnnotation.class, modelsContext ) ).hasSize( 1 );
		assertThat( classDetails.getMetaAnnotated( CustomAnnotation.class, modelsContext ) ).isEmpty();
	}

	@Test
	void testDynamicAttributeCreation() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final Column usage = JpaAnnotations.COLUMN.createUsage( modelsContext );
		// check the attribute defaults
		assertThat( usage.name() ).isEqualTo( "" );
		assertThat( usage.table() ).isEqualTo( "" );
		assertThat( usage.unique() ).isFalse();
		assertThat( usage.nullable() ).isTrue();
		assertThat( usage.insertable() ).isTrue();
		assertThat( usage.updatable() ).isTrue();
		assertThat( usage.columnDefinition() ).isEqualTo( "" );
		assertThat( usage.options() ).isEqualTo( "" );
		assertThat( usage.comment() ).isEqualTo( "" );
		assertThat( usage.length() ).isEqualTo( 255 );
		assertThat( usage.precision() ).isEqualTo( 0 );
		assertThat( usage.scale() ).isEqualTo( 0 );
		assertThat( usage.check() ).isEmpty();
	}

	@Test
	void testNamedAnnotationAccess() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails entityClassDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		final NamedQuery[] namedQueryAnns = entityClassDetails.getRepeatedAnnotationUsages( NamedQuery.class, modelsContext );
		assertThat( namedQueryAnns ).hasSize( 2 );

		final NamedQuery abcAnn = entityClassDetails.getNamedAnnotationUsage( NamedQuery.class, "abc", modelsContext );
		assertThat( abcAnn ).isNotNull();
		assertThat( abcAnn.query() ).isEqualTo( "select me" );
	}

	@Test
	void testFromAnnotations() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		final String query = classDetails.fromAnnotations(
				NamedQuery.class,
				(usage) -> {
					final String name = usage.name();
					if ( "abc".equals( name ) ) {
						return usage.query();
					}
					return null;
				},
				modelsContext
		);
		assertThat( query ).isEqualTo( "select me" );

		final String query2 = classDetails.fromAnnotations(
				NamedQuery.class,
				(usage) -> {
					final String name = usage.name();
					if ( "xyz".equals( name ) ) {
						return usage.query();
					}
					return null;
				},
				modelsContext
		);
		assertThat( query2 ).isEqualTo( "select you" );

		final SecondaryTable secondaryTable = classDetails.fromAnnotations(
				SecondaryTable.class,
				(usage) -> {
					final String name = usage.name();
					if ( "another_table".equals( name ) ) {
						return usage;
					}
					return null;
				},
				modelsContext
		);
		assertThat( secondaryTable ).isNotNull();
	}

	@Test
	void testHasAnnotation() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		// with #hasAnnotationUsage we only get true for the actual repeatable/container form used
		assertThat( classDetails.hasDirectAnnotationUsage( NamedQuery.class ) ).isFalse();
		assertThat( classDetails.hasDirectAnnotationUsage( NamedQueries.class ) ).isTrue();
		assertThat( classDetails.hasDirectAnnotationUsage( SecondaryTable.class ) ).isTrue();
		assertThat( classDetails.hasDirectAnnotationUsage( SecondaryTables.class ) ).isFalse();

		// with #hasRepeatableAnnotationUsage we get true regardless
		assertThat( classDetails.hasAnnotationUsage( NamedQuery.class, modelsContext ) ).isTrue();
		assertThat( classDetails.hasAnnotationUsage( NamedQueries.class, modelsContext ) ).isTrue();
		assertThat( classDetails.hasAnnotationUsage( SecondaryTable.class, modelsContext ) ).isTrue();
		assertThat( classDetails.hasAnnotationUsage( SecondaryTables.class, modelsContext ) ).isFalse();
	}

	@Test
	void testForEachAnnotation() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();

		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		final MutableInteger counter = new MutableInteger();

		classDetails.forEachAnnotationUsage( Entity.class, modelsContext, entityAnnotationUsage -> counter.increment() );
		assertThat( counter.get() ).isEqualTo( 1 );

		counter.set( 0 );
		classDetails.forEachAnnotationUsage( SecondaryTable.class, modelsContext, entityAnnotationUsage -> counter.increment() );
		assertThat( counter.get() ).isEqualTo( 1 );

		counter.set( 0 );
		classDetails.forEachAnnotationUsage( NamedQuery.class, modelsContext, entityAnnotationUsage -> counter.increment() );
		assertThat( counter.get() ).isEqualTo( 2 );
	}

	@Test
	void testGetSingleUsageW() {
		final ModelsContext modelsContext = createModelContext( SimpleEntity.class );
		final ClassDetailsRegistry classDetailsRegistry = modelsContext.getClassDetailsRegistry();
		final ClassDetails classDetails = classDetailsRegistry.getClassDetails( SimpleEntity.class.getName() );

		try {
			classDetails.getAnnotationUsage( NamedQuery.class, modelsContext );
			fail( "Expecting an AnnotationAccessException to be thrown" );
		}
		catch (AnnotationAccessException expected) {
			// this is expected
		}

		final NamedQuery singleAnnotationUsage = classDetails.getDirectAnnotationUsage( NamedQuery.class );
		assertThat( singleAnnotationUsage ).isNull();
	}

}
