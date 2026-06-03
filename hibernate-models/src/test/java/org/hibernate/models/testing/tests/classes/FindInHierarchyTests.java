/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.classes;

import java.util.List;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.MemberDetails;
import org.hibernate.models.spi.MethodDetails;
import org.hibernate.models.spi.ModelsContext;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.buildModelContext;

public class FindInHierarchyTests {

	interface HasName {
		String getName();
	}

	static class Base {
		private int id;

		public int getId() {
			return id;
		}
	}

	static class Mid extends Base implements HasName {
		private String name;

		@Override
		public String getName() {
			return name;
		}
	}

	static class Leaf extends Mid {
		private String description;

		public String getDescription() {
			return description;
		}
	}

	@Test
	void testFindMethodOnClass() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final MethodDetails found = leafDetails.findMethod( m -> "getDescription".equals( m.getName() ) );
		assertThat( found ).isNotNull();
		assertThat( found.getName() ).isEqualTo( "getDescription" );
	}

	@Test
	void testFindMethodReturnsNullWhenNoMatch() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final MethodDetails found = leafDetails.findMethod( m -> "nonExistent".equals( m.getName() ) );
		assertThat( found ).isNull();
	}

	@Test
	void testFindInHierarchyOnSelf() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final FieldDetails found = leafDetails.findInHierarchy( cd -> cd.findFieldByName( "description" ) );
		assertThat( found ).isNotNull();
		assertThat( found.getName() ).isEqualTo( "description" );
	}

	@Test
	void testFindInHierarchyOnSuperclass() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final FieldDetails found = leafDetails.findInHierarchy( cd -> cd.findFieldByName( "id" ) );
		assertThat( found ).isNotNull();
		assertThat( found.getName() ).isEqualTo( "id" );
	}

	@Test
	void testFindInHierarchyOnInterface() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails midDetails = registry.getClassDetails( Mid.class.getName() );
		final MethodDetails found = midDetails.findInHierarchy(
				cd -> cd.findMethod( m -> "getName".equals( m.getName() )
						&& m.getMethodKind() == MethodDetails.MethodKind.GETTER )
		);
		assertThat( found ).isNotNull();
		assertThat( found.getName() ).isEqualTo( "getName" );
	}

	@Test
	void testFindInHierarchyReturnsNullWhenNotFound() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final MemberDetails found = leafDetails.findInHierarchy( cd -> cd.findFieldByName( "nonExistent" ) );
		assertThat( found ).isNull();
	}

	@Test
	void testFindInHierarchyMethodOnSuperclass() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final MethodDetails found = leafDetails.findInHierarchy(
				cd -> cd.findMethod( m -> "getId".equals( m.getName() ) )
		);
		assertThat( found ).isNotNull();
		assertThat( found.getName() ).isEqualTo( "getId" );
	}

	@Test
	void testFindMethodsReturnsAllMatches() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final List<MethodDetails> getters = leafDetails.findMethods(
				m -> m.getMethodKind() == MethodDetails.MethodKind.GETTER
		);
		assertThat( getters ).extracting( MethodDetails::getName )
				.containsExactly( "getDescription" );
	}

	@Test
	void testFindAllInHierarchyCollectsAcrossLevels() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final List<MethodDetails> allGetters = leafDetails.findAllInHierarchy(
				cd -> cd.findMethods( m -> m.getMethodKind() == MethodDetails.MethodKind.GETTER )
		);
		assertThat( allGetters ).isNotEmpty();
		assertThat( allGetters ).extracting( MethodDetails::getName )
				.contains( "getDescription", "getName", "getId" );
	}

	@Test
	void testFindAllInHierarchyReturnsEmptyWhenNotFound() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails leafDetails = registry.getClassDetails( Leaf.class.getName() );
		final List<FieldDetails> results = leafDetails.findAllInHierarchy(
				cd -> {
					final FieldDetails field = cd.findFieldByName( "nonExistent" );
					return field != null ? List.of( field ) : List.of();
				}
		);
		assertThat( results ).isEmpty();
	}

	@Test
	void testFindAllInHierarchyIncludesInterfaces() {
		final ModelsContext modelsContext = buildModelContext( Leaf.class, Mid.class, Base.class, HasName.class );
		final ClassDetailsRegistry registry = modelsContext.getClassDetailsRegistry();

		final ClassDetails midDetails = registry.getClassDetails( Mid.class.getName() );
		final List<MethodDetails> results = midDetails.findAllInHierarchy(
				cd -> cd.findMethods( m -> "getName".equals( m.getName() )
						&& m.getMethodKind() == MethodDetails.MethodKind.GETTER )
		);
		assertThat( results ).hasSizeGreaterThanOrEqualTo( 2 );
	}
}
