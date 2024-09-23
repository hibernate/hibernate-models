/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.generics;

import java.util.Map;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassTypeDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Belladelli
 */
public class NestedRecursiveInheritanceTest {
	@Test
	void testNestedGenericHierarchyWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex(
				Child.class,
				Parent.class,
				ChildHierarchy1.class,
				ParentHierarchy1.class,
				ChildHierarchy2.class,
				ParentHierarchy2.class,
				ChildHierarchy22.class,
				ParentHierarchy22.class
		);
		testNestedGenericHierarchy( index );
	}

	@Test
	void testNestedGenericHierarchyWithoutJandex() {
		testNestedGenericHierarchy( null );
	}

	void testNestedGenericHierarchy(Index index) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Child.class,
				Parent.class,
				ChildHierarchy1.class,
				ParentHierarchy1.class,
				ChildHierarchy2.class,
				ParentHierarchy2.class,
				ChildHierarchy22.class,
				ParentHierarchy22.class
		);

		{
			final ClassDetails child = buildingContext.getClassDetailsRegistry()
					.getClassDetails( Child.class.getName() );
			final ClassDetails child2 = buildingContext.getClassDetailsRegistry()
					.getClassDetails( ChildHierarchy2.class.getName() );
			final ClassDetails child22 = buildingContext.getClassDetailsRegistry()
					.getClassDetails( ChildHierarchy22.class.getName() );

			final FieldDetails parentField = child.findFieldByName( "parent" );
			final TypeDetails parentFieldType = parentField.getType();
			assertThat( parentFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
			assertThat( parentFieldType.determineRawClass().toJavaClass() ).isEqualTo( Parent.class );

			final TypeDetails child2Parent = parentField.resolveRelativeType( child2 );
			assertThat( child2Parent.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
			assertThat( child2Parent.determineRawClass().toJavaClass() ).isEqualTo( ParentHierarchy2.class );

			final TypeDetails child22Parent = parentField.resolveRelativeType( child22 );
			assertThat( child22Parent ).isInstanceOf( ClassTypeDetails.class );
			assertThat( child22Parent.determineRawClass().toJavaClass() ).isEqualTo( ParentHierarchy22.class );
		}

		{
			final ClassDetails parent = buildingContext.getClassDetailsRegistry()
					.getClassDetails( Parent.class.getName() );
			final ClassDetails parent2 = buildingContext.getClassDetailsRegistry()
					.getClassDetails( ParentHierarchy2.class.getName() );
			final ClassDetails parent22 = buildingContext.getClassDetailsRegistry()
					.getClassDetails( ParentHierarchy22.class.getName() );

			final FieldDetails childrenField = parent.findFieldByName( "children" );
			final TypeDetails childrenFieldType = childrenField.getAssociatedType();
			assertThat( childrenFieldType.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
			assertThat( childrenFieldType.determineRawClass().toJavaClass() ).isEqualTo( Child.class );

			final TypeDetails parent2Children = childrenField.resolveRelativeAssociatedType( parent2 );
			assertThat( parent2Children.getTypeKind() ).isEqualTo( TypeDetails.Kind.TYPE_VARIABLE );
			assertThat( parent2Children.determineRawClass().toJavaClass() ).isEqualTo( ChildHierarchy2.class );

			final TypeDetails parent22Children = childrenField.resolveRelativeAssociatedType( parent22 );
			assertThat( parent22Children ).isInstanceOf( ClassTypeDetails.class );
			assertThat( parent22Children.determineRawClass().toJavaClass() ).isEqualTo( ChildHierarchy22.class );
		}
	}

	static abstract class Child<P extends Parent> {
		P parent;
	}

	static abstract class Parent<C extends Child> {
		Map<Long, C> children;
	}

	static class ParentHierarchy1 extends Parent<ChildHierarchy1> {
	}

	static class ChildHierarchy1 extends Child<ParentHierarchy1> {
	}

	static class ChildHierarchy2<P extends ParentHierarchy2> extends Child<P> {
	}

	static class ParentHierarchy2<C extends ChildHierarchy2> extends Parent<C> {
	}

	static class ChildHierarchy22 extends ChildHierarchy2<ParentHierarchy22> {
	}

	static class ParentHierarchy22 extends ParentHierarchy2<ChildHierarchy22> {
	}
}
