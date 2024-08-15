package org.hibernate.models.members;

import java.util.List;

import org.hibernate.models.SourceModelTestHelper;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Marco Belladelli
 */
public class UnboundWildcardTests {

	@Test
	void testWildcardMembersWithJandex() {
		final Index index = SourceModelTestHelper.buildJandexIndex( Thing.class );
		testWildcardMembers( index );
	}

	@Test
	void testWildcardMembersWithoutJandex() {
		testWildcardMembers( null );
	}

	void testWildcardMembers(Index index) {
		final SourceModelBuildingContext buildingContext = SourceModelTestHelper.createBuildingContext(
				index,
				Thing.class
		);

		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry()
				.getClassDetails( Thing.class.getName() );

		{
			final FieldDetails parent = classDetails.findFieldByName( "parent" );
			final TypeDetails parentType = parent.getType();
			assertThat( parentType.isResolved() ).isTrue();
			assertThat( parentType.determineRawClass().toJavaClass() ).isEqualTo( Thing.class );
			final WildcardTypeDetails parentWildcard = parentType.asParameterizedType()
					.getArguments()
					.get( 0 )
					.asWildcardType();
			assertThat( parentWildcard.isResolved() ).isTrue();
			assertThat( parentWildcard.determineRawClass().toJavaClass() ).isEqualTo( Object.class );

			final TypeDetails parentRelativeType = parent.resolveRelativeType( classDetails );
			assertThat( parentRelativeType.isResolved() ).isTrue();
			assertThat( parentRelativeType.determineRawClass().toJavaClass() ).isEqualTo( Thing.class );
			final WildcardTypeDetails parentRelativeWildcard = parentRelativeType.asParameterizedType()
					.getArguments()
					.get( 0 )
					.asWildcardType();
			assertThat( parentRelativeWildcard.isResolved() ).isTrue();
			assertThat( parentRelativeWildcard.determineRawClass().toJavaClass() ).isEqualTo( Object.class );
		}

		{
			final FieldDetails children = classDetails.findFieldByName( "children" );
			final TypeDetails childrenType = children.getType();
			assertThat( childrenType.isResolved() ).isTrue();
			assertThat( childrenType.determineRawClass().toJavaClass() ).isEqualTo( List.class );
			final ParameterizedTypeDetails listType = childrenType.asParameterizedType()
					.getArguments()
					.get( 0 )
					.asParameterizedType();
			assertThat( listType.isResolved() ).isTrue();
			assertThat( listType.determineRawClass().toJavaClass() ).isEqualTo( Thing.class );
			final WildcardTypeDetails childrenWildcard = listType.getArguments().get( 0 ).asWildcardType();
			assertThat( childrenWildcard.isResolved() ).isTrue();
			assertThat( childrenWildcard.determineRawClass().toJavaClass() ).isEqualTo( Object.class );

			final TypeDetails childrenRelativeType = children.resolveRelativeType( classDetails );
			assertThat( childrenRelativeType.isResolved() ).isTrue();
			assertThat( childrenRelativeType.determineRawClass().toJavaClass() ).isEqualTo( List.class );
			final ParameterizedTypeDetails listRelativeType = childrenRelativeType.asParameterizedType()
					.getArguments()
					.get( 0 )
					.asParameterizedType();
			assertThat( listRelativeType.isResolved() ).isTrue();
			assertThat( listRelativeType.determineRawClass().toJavaClass() ).isEqualTo( Thing.class );
			final WildcardTypeDetails childrenRelativeWildcard = listRelativeType.getArguments().get( 0 ).asWildcardType();
			assertThat( childrenRelativeWildcard.isResolved() ).isTrue();
			assertThat( childrenRelativeWildcard.determineRawClass().toJavaClass() ).isEqualTo( Object.class );
		}
	}

	@SuppressWarnings( "unused" )
	static class Thing<T> {
		Thing<?> parent;

		List<Thing<? extends Object>> children;
	}
}
