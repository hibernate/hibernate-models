/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.members;

import java.util.List;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ParameterizedTypeDetails;
import org.hibernate.models.spi.TypeDetails;
import org.hibernate.models.spi.WildcardTypeDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.testing.TestHelper.createModelContext;

/**
 * @author Marco Belladelli
 */
public class UnboundWildcardTests {

	@Test
	void testWildcardMembers() {
		final ModelsContext modelsContext = createModelContext( Thing.class );

		final ClassDetails classDetails = modelsContext.getClassDetailsRegistry()
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
