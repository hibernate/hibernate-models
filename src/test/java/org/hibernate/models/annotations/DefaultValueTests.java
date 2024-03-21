/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */

package org.hibernate.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.AnnotationDescriptorRegistry;
import org.hibernate.models.spi.AnnotationUsage;
import org.hibernate.models.spi.AttributeDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.MutableAnnotationUsage;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class DefaultValueTests {
	@Test
	void testWithJandex() {
		doTest( buildJandexIndex( SimpleEntity.class ) );
	}

	@Test
	void testWithoutJandex() {
		doTest( null );
	}

	private void doTest(Index index) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext( index, TestEntity.class );
		final AnnotationDescriptorRegistry descriptorRegistry = buildingContext.getAnnotationDescriptorRegistry();

		final AnnotationDescriptor<CustomAnnotation> descriptor = descriptorRegistry.getDescriptor( CustomAnnotation.class );
		final List<String> attributeNames = descriptor
				.getAttributes()
				.stream()
				.map( AttributeDescriptor::getName )
				.toList();
		assertThat( attributeNames ).contains( "name", "someClassValue" );
		assertThat( descriptor.findAttribute( "someClassValue" ) ).isNotNull();

		final ClassDetailsRegistry classDetailsRegistry = buildingContext.getClassDetailsRegistry();
		final ClassDetails entityClassDetails = classDetailsRegistry.getClassDetails( TestEntity.class.getName() );
		final AnnotationUsage<CustomAnnotation> annotationUsage = entityClassDetails.getAnnotationUsage( CustomAnnotation.class );
		assertThat( annotationUsage.getClassDetails( "someClassValue" ) ).isNotNull();
		assertThat( annotationUsage.getClassDetails( "someClassValue" ).toJavaClass() ).isEqualTo( Entity.class );

		final MutableAnnotationUsage<CustomAnnotation> created = descriptor.createUsage( null, buildingContext );
	}

	@Target( ElementType.TYPE )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface CustomAnnotation {
		String name();
		Class someClassValue();
	}

	@Entity(name="TestEntity")
	@Table(name="TestEntity")
	@CustomAnnotation( name="tester", someClassValue = Entity.class )
	public static class TestEntity {
		@Id
		private Integer id;
		private String name;
	}
}
