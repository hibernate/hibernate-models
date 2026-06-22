/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.orm;

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
import org.hibernate.models.CompleteAnnotationDescriptor;
import org.hibernate.models.internal.OrmAnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationTarget.Kind;

import java.lang.annotation.Annotation;
import java.util.EnumSet;
import java.util.function.Consumer;

import static org.hibernate.models.Creator.createCompleteAnnotationDescriptor;

/**
 * Descriptors for JPA annotations
 *
 * @author Steve Ebersole
 */
@SuppressWarnings("unused")
public interface JpaAnnotations {
	// to test the deprecated descriptor
	@SuppressWarnings("removal")
	AnnotationDescriptor<Entity> ENTITY = new OrmAnnotationDescriptor<>(
			Entity.class,
			EntityAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);

	// to test direct instantiation
	AnnotationDescriptor<Embeddable> EMBEDDABLE = new CompleteAnnotationDescriptor<>(
			Embeddable.class,
			EmbeddableAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);

	AnnotationDescriptor<Id> ID = createCompleteAnnotationDescriptor(
			Id.class,
			IdAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false
	);

	AnnotationDescriptor<SequenceGenerators> SEQUENCE_GENERATORS = createCompleteAnnotationDescriptor(
			SequenceGenerators.class,
			SequenceGeneratorsAnnotation.class,
			EnumSet.of( Kind.CLASS, Kind.METHOD, Kind.FIELD, Kind.PACKAGE ),
			false
	);
	AnnotationDescriptor<SequenceGenerator> SEQUENCE_GENERATOR = createCompleteAnnotationDescriptor(
			SequenceGenerator.class,
			SequenceGeneratorAnnotation.class,
			EnumSet.of( Kind.CLASS, Kind.METHOD, Kind.FIELD, Kind.PACKAGE ),
			false,
			SEQUENCE_GENERATORS
	);

	AnnotationDescriptor<Basic> BASIC = createCompleteAnnotationDescriptor(
			Basic.class,
			BasicAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false
	);
	AnnotationDescriptor<Embedded> EMBEDDED = createCompleteAnnotationDescriptor(
			Embedded.class,
			EmbeddedAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false
	);
	AnnotationDescriptor<ElementCollection> ELEMENT_COLLECTION = createCompleteAnnotationDescriptor(
			ElementCollection.class,
			ElementCollectionJpaAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);

	AnnotationDescriptor<NamedQueries> NAMED_QUERIES = createCompleteAnnotationDescriptor(
			NamedQueries.class,
			NamedQueriesAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);
	AnnotationDescriptor<NamedQuery> NAMED_QUERY = createCompleteAnnotationDescriptor(
			NamedQuery.class,
			NamedQueryAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false,
			NAMED_QUERIES
	);

	AnnotationDescriptor<NamedNativeQueries> NAMED_NATIVE_QUERIES = createCompleteAnnotationDescriptor(
			NamedNativeQueries.class,
			NamedNativeQueriesAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);
	AnnotationDescriptor<NamedNativeQuery> NAMED_NATIVE_QUERY = createCompleteAnnotationDescriptor(
			NamedNativeQuery.class,
			NamedNativeQueryAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false,
			NAMED_NATIVE_QUERIES
	);

	AnnotationDescriptor<Table> TABLE = createCompleteAnnotationDescriptor(
			Table.class,
			TableAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);
	AnnotationDescriptor<SecondaryTables> SECONDARY_TABLES = createCompleteAnnotationDescriptor(
			SecondaryTables.class,
			SecondaryTablesAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);
	AnnotationDescriptor<SecondaryTable> SECONDARY_TABLE = createCompleteAnnotationDescriptor(
			SecondaryTable.class,
			SecondaryTableAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false,
			SECONDARY_TABLES
	);
	AnnotationDescriptor<CollectionTable> COLLECTION_TABLE = createCompleteAnnotationDescriptor(
			CollectionTable.class,
			CollectionTableAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false
	);

	AnnotationDescriptor<Column> COLUMN = createCompleteAnnotationDescriptor(
			Column.class,
			ColumnAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false
	);

	AnnotationDescriptor<JoinColumns> JOIN_COLUMNS = createCompleteAnnotationDescriptor(
			JoinColumns.class,
			JoinColumnsAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false
	);
	AnnotationDescriptor<JoinColumn> JOIN_COLUMN = createCompleteAnnotationDescriptor(
			JoinColumn.class,
			JoinColumnAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false,
			JOIN_COLUMNS
	);

	AnnotationDescriptor<PrimaryKeyJoinColumns> PRIMARY_KEY_JOIN_COLUMNS = createCompleteAnnotationDescriptor(
			PrimaryKeyJoinColumns.class,
			PrimaryKeyJoinColumnsJpaAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false
	);
	AnnotationDescriptor<PrimaryKeyJoinColumn> PRIMARY_KEY_JOIN_COLUMN = createCompleteAnnotationDescriptor(
			PrimaryKeyJoinColumn.class,
			PrimaryKeyJoinColumnJpaAnnotation.class,
			EnumSet.of( Kind.METHOD, Kind.FIELD ),
			false,
			PRIMARY_KEY_JOIN_COLUMNS
	);

	AnnotationDescriptor<CheckConstraint> CHECK_CONSTRAINT = createCompleteAnnotationDescriptor(
			CheckConstraint.class,
			CheckConstraintAnnotation.class,
			EnumSet.noneOf( Kind.class ),
			false
	);
	AnnotationDescriptor<ForeignKey> FOREIGN_KEY = createCompleteAnnotationDescriptor(
			ForeignKey.class,
			ForeignKeyAnnotation.class,
			EnumSet.noneOf( Kind.class ),
			false
	);
	AnnotationDescriptor<UniqueConstraint> UNIQUE_CONSTRAINT = createCompleteAnnotationDescriptor(
			UniqueConstraint.class,
			UniqueConstraintAnnotation.class,
			EnumSet.noneOf( Kind.class ),
			false
	);
	AnnotationDescriptor<Index> INDEX = createCompleteAnnotationDescriptor(
			Index.class,
			IndexAnnotation.class,
			EnumSet.noneOf( Kind.class ),
			false
	);

	AnnotationDescriptor<Cacheable> CACHEABLE = createCompleteAnnotationDescriptor(
			Cacheable.class,
			CacheableAnnotation.class,
			EnumSet.of( Kind.CLASS ),
			false
	);
	AnnotationDescriptor<Transient> TRANSIENT = createCompleteAnnotationDescriptor(
			Transient.class,
			TransientAnnotation.class,
			EnumSet.of( Kind.FIELD, Kind.METHOD ),
			false
	);


	static void forEachAnnotation(Consumer<AnnotationDescriptor<? extends Annotation>> consumer) {
		OrmAnnotationHelper.forEachOrmAnnotation( JpaAnnotations.class, consumer );
	}
}
