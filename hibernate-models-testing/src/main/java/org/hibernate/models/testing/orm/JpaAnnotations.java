/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.function.Consumer;

import org.hibernate.models.spi.ExplicitAnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptor;

import jakarta.persistence.Basic;
import jakarta.persistence.Cacheable;
import jakarta.persistence.CheckConstraint;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.NamedNativeQueries;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.PrimaryKeyJoinColumns;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.SequenceGenerators;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.models.spi.AnnotationTarget.Kind;

/**
 * Descriptors for JPA annotations
 *
 * @author Steve Ebersole
 */
@SuppressWarnings("unused")
public interface JpaAnnotations {
	AnnotationDescriptor<Entity> ENTITY = new ExplicitAnnotationDescriptor<>( Entity.class, EntityAnnotation.class, EnumSet.of( Kind.CLASS ), false );
	AnnotationDescriptor<Embeddable> EMBEDDABLE = new ExplicitAnnotationDescriptor<>( Embeddable.class, EmbeddableAnnotation.class, EnumSet.of( Kind.CLASS ), false );

	AnnotationDescriptor<Id> ID = new ExplicitAnnotationDescriptor<>( Id.class, IdAnnotation.class, EnumSet.of( Kind.METHOD, Kind.FIELD ), false );

	AnnotationDescriptor<SequenceGenerators> SEQUENCE_GENERATORS = new ExplicitAnnotationDescriptor<>( SequenceGenerators.class, SequenceGeneratorsAnnotation.class, EnumSet.of( Kind.CLASS, Kind.METHOD, Kind.FIELD, Kind.PACKAGE ), false );
	AnnotationDescriptor<SequenceGenerator> SEQUENCE_GENERATOR = new ExplicitAnnotationDescriptor<>( SequenceGenerator.class, SequenceGeneratorAnnotation.class, EnumSet.of( Kind.CLASS, Kind.METHOD, Kind.FIELD, Kind.PACKAGE ), false, SEQUENCE_GENERATORS );

	AnnotationDescriptor<Basic> BASIC = new ExplicitAnnotationDescriptor<>( Basic.class, BasicAnnotation.class );
	AnnotationDescriptor<Embedded> EMBEDDED = new ExplicitAnnotationDescriptor<>( Embedded.class, EmbeddedAnnotation.class );
	AnnotationDescriptor<ElementCollection> ELEMENT_COLLECTION = new ExplicitAnnotationDescriptor<>( ElementCollection.class, ElementCollectionJpaAnnotation.class );

	AnnotationDescriptor<NamedQueries> NAMED_QUERIES = new ExplicitAnnotationDescriptor<>( NamedQueries.class, NamedQueriesAnnotation.class );
	AnnotationDescriptor<NamedQuery> NAMED_QUERY = new ExplicitAnnotationDescriptor<>( NamedQuery.class, NamedQueryAnnotation.class, NAMED_QUERIES );

	AnnotationDescriptor<NamedNativeQueries> NAMED_NATIVE_QUERIES = new ExplicitAnnotationDescriptor<>( NamedNativeQueries.class, NamedNativeQueriesAnnotation.class );
	AnnotationDescriptor<NamedNativeQuery> NAMED_NATIVE_QUERY = new ExplicitAnnotationDescriptor<>( NamedNativeQuery.class, NamedNativeQueryAnnotation.class, NAMED_NATIVE_QUERIES );

	AnnotationDescriptor<Table> TABLE = new ExplicitAnnotationDescriptor<>( Table.class, TableAnnotation.class );
	AnnotationDescriptor<SecondaryTables> SECONDARY_TABLES = new ExplicitAnnotationDescriptor<>( SecondaryTables.class, SecondaryTablesAnnotation.class );
	AnnotationDescriptor<SecondaryTable> SECONDARY_TABLE = new ExplicitAnnotationDescriptor<>( SecondaryTable.class, SecondaryTableAnnotation.class, SECONDARY_TABLES );
	AnnotationDescriptor<CollectionTable> COLLECTION_TABLE = new ExplicitAnnotationDescriptor<>( CollectionTable.class, CollectionTableAnnotation.class );

	AnnotationDescriptor<Column> COLUMN = new ExplicitAnnotationDescriptor<>( Column.class, ColumnAnnotation.class );

	AnnotationDescriptor<JoinColumns> JOIN_COLUMNS = new ExplicitAnnotationDescriptor<>( JoinColumns.class, JoinColumnsAnnotation.class );
	AnnotationDescriptor<JoinColumn> JOIN_COLUMN = new ExplicitAnnotationDescriptor<>( JoinColumn.class, JoinColumnAnnotation.class, JOIN_COLUMNS );

	AnnotationDescriptor<PrimaryKeyJoinColumns> PRIMARY_KEY_JOIN_COLUMNS = new ExplicitAnnotationDescriptor<>( PrimaryKeyJoinColumns.class, PrimaryKeyJoinColumnsJpaAnnotation.class );
	AnnotationDescriptor<PrimaryKeyJoinColumn> PRIMARY_KEY_JOIN_COLUMN = new ExplicitAnnotationDescriptor<>( PrimaryKeyJoinColumn.class, PrimaryKeyJoinColumnJpaAnnotation.class, PRIMARY_KEY_JOIN_COLUMNS );

	AnnotationDescriptor<CheckConstraint> CHECK_CONSTRAINT = new ExplicitAnnotationDescriptor<>( CheckConstraint.class, CheckConstraintAnnotation.class );
	AnnotationDescriptor<ForeignKey> FOREIGN_KEY = new ExplicitAnnotationDescriptor<>( ForeignKey.class, ForeignKeyAnnotation.class );
	AnnotationDescriptor<UniqueConstraint> UNIQUE_CONSTRAINT = new ExplicitAnnotationDescriptor<>( UniqueConstraint.class, UniqueConstraintAnnotation.class );
	AnnotationDescriptor<Index> INDEX = new ExplicitAnnotationDescriptor<>( Index.class, IndexAnnotation.class );

	AnnotationDescriptor<Cacheable> CACHEABLE = new ExplicitAnnotationDescriptor<>( Cacheable.class, CacheableAnnotation.class );
	AnnotationDescriptor<Transient> TRANSIENT = new ExplicitAnnotationDescriptor<>( Transient.class, TransientAnnotation.class );

//	AnnotationDescriptor<Access> ACCESS = createOrmDescriptor( Access.class );
//	AnnotationDescriptor<AssociationOverrides> ASSOCIATION_OVERRIDES = createOrmDescriptor( AssociationOverrides.class );
//	AnnotationDescriptor<AssociationOverride> ASSOCIATION_OVERRIDE = createOrmDescriptor( AssociationOverride.class, ASSOCIATION_OVERRIDES );
//	AnnotationDescriptor<AttributeOverrides> ATTRIBUTE_OVERRIDES = createOrmDescriptor( AttributeOverrides.class );
//	AnnotationDescriptor<AttributeOverride> ATTRIBUTE_OVERRIDE = createOrmDescriptor( AttributeOverride.class, ATTRIBUTE_OVERRIDES );
//	AnnotationDescriptor<Basic> BASIC = createOrmDescriptor( Basic.class );
//	AnnotationDescriptor<Cacheable> CACHEABLE = createOrmDescriptor( Cacheable.class );
//	AnnotationDescriptor<Column> COLUMN = createOrmDescriptor( Column.class );
//	AnnotationDescriptor<ColumnResult> COLUMN_RESULT = createOrmDescriptor( ColumnResult.class );
//	AnnotationDescriptor<Converts> CONVERTS = createOrmDescriptor( Converts.class );
//	AnnotationDescriptor<Convert> CONVERT = createOrmDescriptor( Convert.class, CONVERTS );
//	AnnotationDescriptor<Converter> CONVERTER = createOrmDescriptor( Converter.class );
//	AnnotationDescriptor<DiscriminatorColumn> DISCRIMINATOR_COLUMN = createOrmDescriptor( DiscriminatorColumn.class );
//	AnnotationDescriptor<DiscriminatorValue> DISCRIMINATOR_VALUE = createOrmDescriptor( DiscriminatorValue.class );
//	AnnotationDescriptor<Embeddable> EMBEDDABLE = createOrmDescriptor( Embeddable.class );
//	AnnotationDescriptor<Embedded> EMBEDDED = createOrmDescriptor( Embedded.class );
//	AnnotationDescriptor<EmbeddedId> EMBEDDED_ID = createOrmDescriptor( EmbeddedId.class );
//	AnnotationDescriptor<Entity> ENTITY = createOrmDescriptor( Entity.class );
//	AnnotationDescriptor<EntityListeners> ENTITY_LISTENERS = createOrmDescriptor( EntityListeners.class );
//	AnnotationDescriptor<EntityResult> ENTITY_RESULT = createOrmDescriptor( EntityResult.class );
//	AnnotationDescriptor<Enumerated> ENUMERATED = createOrmDescriptor( Enumerated.class );
//	AnnotationDescriptor<ExcludeDefaultListeners> EXCLUDE_DEFAULT_LISTENERS = createOrmDescriptor( ExcludeDefaultListeners.class );
//	AnnotationDescriptor<ExcludeSuperclassListeners> EXCLUDE_SUPERCLASS_LISTENERS = createOrmDescriptor( ExcludeSuperclassListeners.class );
//	AnnotationDescriptor<FieldResult> FIELD_RESULT = createOrmDescriptor( FieldResult.class );
//	AnnotationDescriptor<ForeignKey> FOREIGN_KEY = createOrmDescriptor( ForeignKey.class );
//	AnnotationDescriptor<GeneratedValue> GENERATED_VALUE = createOrmDescriptor( GeneratedValue.class );
//	AnnotationDescriptor<Id> ID = createOrmDescriptor( Id.class );
//	AnnotationDescriptor<IdClass> ID_CLASS = createOrmDescriptor( IdClass.class );
//	AnnotationDescriptor<Index> INDEX = createOrmDescriptor( Index.class );
//	AnnotationDescriptor<Inheritance> INHERITANCE = createOrmDescriptor( Inheritance.class );
//	AnnotationDescriptor<JoinColumns> JOIN_COLUMNS = createOrmDescriptor( JoinColumns.class );
//	AnnotationDescriptor<JoinColumn> JOIN_COLUMN = createOrmDescriptor( JoinColumn.class, JOIN_COLUMNS );
//	AnnotationDescriptor<JoinTable> JOIN_TABLE = createOrmDescriptor( JoinTable.class );
//	AnnotationDescriptor<Lob> LOB = createOrmDescriptor( Lob.class );
//	AnnotationDescriptor<ManyToMany> MANY_TO_MANY = createOrmDescriptor( ManyToMany.class );
//	AnnotationDescriptor<ManyToOne> MANY_TO_ONE = createOrmDescriptor( ManyToOne.class );
//	AnnotationDescriptor<MapKey> MAP_KEY = createOrmDescriptor( MapKey.class );
//	AnnotationDescriptor<MapKeyClass> MAP_KEY_CLASS = createOrmDescriptor( MapKeyClass.class );
//	AnnotationDescriptor<MapKeyColumn> MAP_KEY_COLUMN = createOrmDescriptor( MapKeyColumn.class );
//	AnnotationDescriptor<MapKeyEnumerated> MAP_KEY_ENUMERATED = createOrmDescriptor( MapKeyEnumerated.class );
//	AnnotationDescriptor<MapKeyJoinColumns> MAP_KEY_JOIN_COLUMNS = createOrmDescriptor( MapKeyJoinColumns.class );
//	AnnotationDescriptor<MapKeyJoinColumn> MAP_KEY_JOIN_COLUMN = createOrmDescriptor( MapKeyJoinColumn.class, MAP_KEY_JOIN_COLUMNS );
//	AnnotationDescriptor<MapKeyTemporal> MAP_KEY_TEMPORAL = createOrmDescriptor( MapKeyTemporal.class );
//	AnnotationDescriptor<MappedSuperclass> MAPPED_SUPERCLASS = createOrmDescriptor( MappedSuperclass.class );
//	AnnotationDescriptor<MapsId> MAPS_ID = createOrmDescriptor( MapsId.class );
//	AnnotationDescriptor<NamedAttributeNode> NAMED_ATTRIBUTE_NODE = createOrmDescriptor( NamedAttributeNode.class );
//	AnnotationDescriptor<NamedEntityGraphs> NAMED_ENTITY_GRAPHS = createOrmDescriptor( NamedEntityGraphs.class );
//	AnnotationDescriptor<NamedEntityGraph> NAMED_ENTITY_GRAPH = createOrmDescriptor( NamedEntityGraph.class, NAMED_ENTITY_GRAPHS );
//	AnnotationDescriptor<NamedNativeQueries> NAMED_NATIVE_QUERIES = createOrmDescriptor( NamedNativeQueries.class );
//	AnnotationDescriptor<NamedNativeQuery> NAMED_NATIVE_QUERY = createOrmDescriptor( NamedNativeQuery.class, NAMED_NATIVE_QUERIES );
//	AnnotationDescriptor<NamedQueries> NAMED_QUERIES = createOrmDescriptor( NamedQueries.class );
//	AnnotationDescriptor<NamedQuery> NAMED_QUERY = createOrmDescriptor( NamedQuery.class, NAMED_QUERIES );
//	AnnotationDescriptor<NamedStoredProcedureQueries> NAMED_STORED_PROCEDURE_QUERIES = createOrmDescriptor( NamedStoredProcedureQueries.class );
//	AnnotationDescriptor<NamedStoredProcedureQuery> NAMED_STORED_PROCEDURE_QUERY = createOrmDescriptor( NamedStoredProcedureQuery.class, NAMED_STORED_PROCEDURE_QUERIES );
//	AnnotationDescriptor<NamedSubgraph> NAMED_SUB_GRAPH = createOrmDescriptor( NamedSubgraph.class );
//	AnnotationDescriptor<OneToMany> ONE_TO_MANY = createOrmDescriptor( OneToMany.class );
//	AnnotationDescriptor<OneToOne> ONE_TO_ONE = createOrmDescriptor( OneToOne.class );
//	AnnotationDescriptor<OrderBy> ORDER_BY = createOrmDescriptor( OrderBy.class );
//	AnnotationDescriptor<OrderColumn> ORDER_COLUMN = createOrmDescriptor( OrderColumn.class );
//	AnnotationDescriptor<PostLoad> POST_LOAD = createOrmDescriptor( PostLoad.class );
//	AnnotationDescriptor<PostPersist> POST_PERSIST = createOrmDescriptor( PostPersist.class );
//	AnnotationDescriptor<PostRemove> POST_REMOVE = createOrmDescriptor( PostRemove.class );
//	AnnotationDescriptor<PostUpdate> POST_UPDATE = createOrmDescriptor( PostUpdate.class );
//	AnnotationDescriptor<PrePersist> PRE_PERSIST = createOrmDescriptor( PrePersist.class );
//	AnnotationDescriptor<PreRemove> PRE_REMOVE = createOrmDescriptor( PreRemove.class );
//	AnnotationDescriptor<PreUpdate> PRE_UPDATE = createOrmDescriptor( PreUpdate.class );
//	AnnotationDescriptor<PrimaryKeyJoinColumns> PRIMARY_KEY_JOIN_COLUMNS = createOrmDescriptor( PrimaryKeyJoinColumns.class );
//	AnnotationDescriptor<PrimaryKeyJoinColumn> PRIMARY_KEY_JOIN_COLUMN = createOrmDescriptor( PrimaryKeyJoinColumn.class, PRIMARY_KEY_JOIN_COLUMNS );
//	AnnotationDescriptor<QueryHint> QUERY_HINT = createOrmDescriptor( QueryHint.class );
//	AnnotationDescriptor<SecondaryTables> SECONDARY_TABLES = createOrmDescriptor( SecondaryTables.class );
//	AnnotationDescriptor<SecondaryTable> SECONDARY_TABLE = createOrmDescriptor( SecondaryTable.class, SECONDARY_TABLES );
//	AnnotationDescriptor<SequenceGenerators> SEQUENCE_GENERATORS = createOrmDescriptor( SequenceGenerators.class );
//	AnnotationDescriptor<SequenceGenerator> SEQUENCE_GENERATOR = createOrmDescriptor( SequenceGenerator.class, SEQUENCE_GENERATORS );
//	AnnotationDescriptor<SqlResultSetMappings> SQL_RESULT_SET_MAPPINGS = createOrmDescriptor( SqlResultSetMappings.class );
//	AnnotationDescriptor<SqlResultSetMapping> SQL_RESULT_SET_MAPPING = createOrmDescriptor( SqlResultSetMapping.class, SQL_RESULT_SET_MAPPINGS );
//	AnnotationDescriptor<StoredProcedureParameter> STORED_PROCEDURE_PARAMETER = createOrmDescriptor( StoredProcedureParameter.class );
//	AnnotationDescriptor<Table> TABLE = createOrmDescriptor( Table.class );
//	AnnotationDescriptor<CheckConstraint> CHECK_CONSTRAINT = createOrmDescriptor( CheckConstraint.class );
//	AnnotationDescriptor<TableGenerators> TABLE_GENERATORS = createOrmDescriptor( TableGenerators.class );
//	AnnotationDescriptor<TableGenerator> TABLE_GENERATOR = createOrmDescriptor( TableGenerator.class, TABLE_GENERATORS );
//	AnnotationDescriptor<Temporal> TEMPORAL = createOrmDescriptor( Temporal.class );
//	AnnotationDescriptor<Transient> TRANSIENT = createOrmDescriptor( Transient.class );
//	AnnotationDescriptor<UniqueConstraint> UNIQUE_CONSTRAINT = createOrmDescriptor( UniqueConstraint.class );
//	AnnotationDescriptor<Version> VERSION = createOrmDescriptor( Version.class );

	static void forEachAnnotation(Consumer<AnnotationDescriptor<? extends Annotation>> consumer) {
		OrmAnnotationHelper.forEachOrmAnnotation( JpaAnnotations.class, consumer );
	}
}
