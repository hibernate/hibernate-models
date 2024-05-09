package org.hibernate.models.annotations;

import java.lang.reflect.Modifier;

import org.hibernate.models.AnnotationAccessException;
import org.hibernate.models.internal.SourceModelBuildingContextImpl;
import org.hibernate.models.spi.ClassDetails;

import org.junit.jupiter.api.Test;

import org.jboss.jandex.Index;

import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.hibernate.models.SourceModelTestHelper.buildJandexIndex;
import static org.hibernate.models.SourceModelTestHelper.createBuildingContext;

/**
 * @author Steve Ebersole
 */
public class RepeatableUsageTests {
	@Test
	void testModifiers() {
		int fieldModifiers = Modifier.fieldModifiers();
		assertThat( Modifier.isAbstract( fieldModifiers ) ).isFalse();
		fieldModifiers |= Modifier.ABSTRACT;
		assertThat( Modifier.isAbstract( fieldModifiers ) ).isTrue();
	}

	@Test
	void baseline() {
		final NamedQuery query = Thing4.class.getAnnotation( NamedQuery.class );
		final NamedQueries queries = Thing4.class.getAnnotation( NamedQueries.class );
		assertThat( query ).isNotNull();
		assertThat( queries ).isNotNull();
		assertThat( queries.value() ).hasSize( 2 );
	}

	@Test
	void testWithJandex() {
		verify( buildJandexIndex( Thing1.class, Thing2.class, Thing3.class, Thing4.class ) );
	}

	@Test
	void testWithoutJandex() {
		verify( null );
	}

	private void verify(Index jandexIndex) {
		final SourceModelBuildingContextImpl buildingContext = createBuildingContext(
				jandexIndex,
				Thing1.class,
				Thing2.class,
				Thing3.class,
				Thing4.class,
				Thing5.class
		);

		verifyThing1( buildingContext );
		verifyThing2( buildingContext );
		verifyThing3( buildingContext );
		verifyThing4( buildingContext );
		verifyThing5( buildingContext );
	}

	private void verifyThing1(SourceModelBuildingContextImpl buildingContext) {
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Thing1.class.getName() );

		try {
			classDetails.getAnnotationUsage( NamedQuery.class, buildingContext );
			fail( "Expecting failure" );
		}
		catch (AnnotationAccessException expected) {
		}

		final NamedQuery[] usages = classDetails.getRepeatedAnnotationUsages( NamedQuery.class, buildingContext );
		assertThat( usages ).hasSize( 2 );

		final NamedQueries containerUsage = classDetails.getAnnotationUsage( NamedQueries.class, buildingContext );
		assertThat( containerUsage ).isNotNull();
	}

	private void verifyThing2(SourceModelBuildingContextImpl buildingContext) {
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Thing2.class.getName() );

		try {
			classDetails.getAnnotationUsage( NamedQuery.class, buildingContext );
			fail( "Expecting failure" );
		}
		catch (AnnotationAccessException expected) {
		}

		final NamedQuery[] usages = classDetails.getRepeatedAnnotationUsages( NamedQuery.class, buildingContext );
		assertThat( usages ).hasSize( 2 );

		final NamedQueries containerUsage = classDetails.getAnnotationUsage( NamedQueries.class, buildingContext );
		assertThat( containerUsage ).isNotNull();
	}

	private void verifyThing3(SourceModelBuildingContextImpl buildingContext) {
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Thing3.class.getName() );

		assertThat( classDetails.getAnnotationUsage( NamedQuery.class, buildingContext ) ).isNull();
		assertThat( classDetails.getAnnotationUsage( NamedQueries.class, buildingContext ) ).isNull();
	}

	private void verifyThing4(SourceModelBuildingContextImpl buildingContext) {
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Thing4.class.getName() );

		// NOTE : this works like the JDK call, though we may want to make this more sane
		final NamedQuery usage = classDetails.getAnnotationUsage( NamedQuery.class, buildingContext );
		assertThat( usage ).isNotNull();

		final NamedQuery[] usages = classDetails.getRepeatedAnnotationUsages( NamedQuery.class, buildingContext );
		assertThat( usages ).hasSize( 3 );

		final NamedQueries containerUsage = classDetails.getAnnotationUsage( NamedQueries.class, buildingContext );
		assertThat( containerUsage ).isNotNull();
	}

	private void verifyThing5(SourceModelBuildingContextImpl buildingContext) {
		final ClassDetails classDetails = buildingContext.getClassDetailsRegistry().getClassDetails( Thing5.class.getName() );

		final NamedQueries containerUsage = classDetails.getAnnotationUsage( NamedQueries.class, buildingContext );
		assertThat( containerUsage ).isNotNull();

		final NamedQuery usage = classDetails.getAnnotationUsage( NamedQuery.class, buildingContext );
		assertThat( usage ).isNotNull();

		final NamedQuery[] usages = classDetails.getRepeatedAnnotationUsages( NamedQuery.class, buildingContext );
		assertThat( usages ).hasSize( 1 );
	}

	@NamedQuery(name="qry1", query = "from Thing")
	@NamedQuery(name="qry2", query = "from Thing")
	public static class Thing1 {
	}

	@NamedQueries({
			@NamedQuery(name = "qry1", query = "from Thing"),
			@NamedQuery(name = "qry2", query = "from Thing")
	})
	public static class Thing2 {
	}

	public static class Thing3 {
	}

	@NamedQueries({
			@NamedQuery(name = "qry1", query = "from Thing"),
			@NamedQuery(name = "qry2", query = "from Thing")
	})
	@NamedQuery(name="qry3", query = "from Thing")
	public static class Thing4 {
	}

	@NamedQueries({
			@NamedQuery(name = "qry1", query = "from Thing")
	})
	public static class Thing5 {
	}
}
