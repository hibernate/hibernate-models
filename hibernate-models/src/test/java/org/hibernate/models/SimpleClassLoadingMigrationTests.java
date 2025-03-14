package org.hibernate.models;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.junit.jupiter.api.Test;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.models.internal.SimpleClassLoading.SIMPLE_CLASS_LOADING;

/**
 * @author Steve Ebersole
 */
public class SimpleClassLoadingMigrationTests {
	protected SourceModelBuildingContext createModelContext(Class<?>... classes) {
		return SourceModelTestHelper.createBuildingContext( classes );
	}

	@Test
	void testSimpleMigration() {
		final SourceModelBuildingContext modelContext = createModelContext( SimpleSerializationTests.SimpleClassWithAnnotations.class );
		final ClassDetails classDetails = modelContext.getClassDetailsRegistry().findClassDetails( SimpleSerializationTests.SimpleClassWithAnnotations.class.getName() );
		assertThat( classDetails ).isNotNull();

		final CollectingClassLoading collectingClassLoading = new CollectingClassLoading();
		classDetails.toJavaClass( collectingClassLoading, modelContext );

		assertThat( collectingClassLoading.getClassesByName() ).isNotEmpty();
	}

	@Test
	void testSimpleMembersMigration() {
		final SourceModelBuildingContext modelContext = createModelContext( SimpleSerializationTests.SimpleClassWithMembers.class );
		final ClassDetails classDetails = modelContext.getClassDetailsRegistry().findClassDetails( SimpleSerializationTests.SimpleClassWithMembers.class.getName() );
		assertThat( classDetails ).isNotNull();

		final CollectingClassLoading collectingClassLoading = new CollectingClassLoading();
		final Class<?> javaClass = classDetails.toJavaClass( collectingClassLoading, modelContext );

		assertThat( collectingClassLoading.getClassesByName() ).hasSize( 1 );

		final FieldDetails fieldByName = classDetails.findFieldByName( "anInt" );
		final Field anIntField = fieldByName.toJavaMember( javaClass, collectingClassLoading, modelContext );
		assertThat( anIntField.getDeclaringClass() ).isSameAs( javaClass );
	}

	@Entity(name="Thing")
	@Table(name="Thing")
	public static class Thing {
		@Id
		private Integer id;
		private String name;

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	private static class CollectingClassLoading implements ClassLoading {
		private final Map<String,Class<?>> classesByName = new HashMap<>();

		public Map<String, Class<?>> getClassesByName() {
			return classesByName;
		}

		@Override
		public <T> Class<T> classForName(String name) {
			final Class<T> classForName = SIMPLE_CLASS_LOADING.classForName( name );
			classesByName.put( name, classForName );
			return classForName;
		}

		@Override
		public <T> Class<T> findClassForName(String name) {
			final Class<T> classForName = SIMPLE_CLASS_LOADING.findClassForName( name );
			classesByName.put( name, classForName );
			return classForName;
		}

		@Override
		public URL locateResource(String resourceName) {
			throw new UnsupportedOperationException();
		}

		@Override
		public <S> Collection<S> loadJavaServices(Class<S> serviceType) {
			throw new UnsupportedOperationException();
		}
	}
}
