/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.module.Configuration;
import java.lang.module.ModuleFinder;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.BasicModelsContextImpl;
import org.hibernate.models.spi.AnnotationTarget;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.ModuleDetails;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Steve Ebersole
 */
public class ModuleTests {
	@Test
	void moduleElementTypeIsRecognized() {
		assertThat( AnnotationTarget.Kind.from( ElementType.MODULE ) ).isEqualTo( AnnotationTarget.Kind.MODULE );
		assertThat( AnnotationTarget.Kind.from( new ElementType[] { ElementType.MODULE } ) )
				.containsExactly( AnnotationTarget.Kind.MODULE );
	}

	@Test
	void testModuleAnnotationUsage(@TempDir Path tempDir) throws Exception {
		final ModuleLayer layer = compileAndLoadTestModules( tempDir );
		final Module subjectModule = layer.findModule( "test.module.subject" ).orElseThrow();
		final Class<? extends Annotation> markerType = loadMarkerAnnotation( layer );

		final ModelsContext modelsContext = new BasicModelsContextImpl(
				new ModuleLayerClassLoading( layer ),
				true,
				null
		);

		final ModuleDetails moduleDetails = modelsContext.getModuleDetailsRegistry().resolveModuleDetails( subjectModule );
		assertThat( moduleDetails.getKind() ).isEqualTo( AnnotationTarget.Kind.MODULE );
		assertThat( moduleDetails.getModuleName() ).isEqualTo( "test.module.subject" );
		assertThat( moduleDetails.asModuleDetails() ).isSameAs( moduleDetails );

		final Annotation usage = moduleDetails.getAnnotationUsage( markerType, modelsContext );
		assertThat( usage ).isNotNull();

		final Method valueMethod = markerType.getMethod( "value" );
		assertThat( valueMethod.invoke( usage ) ).isEqualTo( "module-level" );
	}

	@SuppressWarnings("unchecked")
	private static Class<? extends Annotation> loadMarkerAnnotation(ModuleLayer layer) throws ClassNotFoundException {
		return (Class<? extends Annotation>) layer.findLoader( "test.module.annotations" )
				.loadClass( "test.module.annotations.ModuleMarker" );
	}

	private static ModuleLayer compileAndLoadTestModules(Path tempDir) throws IOException {
		final Path sourceDir = tempDir.resolve( "src" );
		final Path classesDir = tempDir.resolve( "classes" );

		writeSource(
				sourceDir.resolve( "test.module.annotations/module-info.java" ),
				"""
						module test.module.annotations {
							exports test.module.annotations;
						}
						"""
		);
		writeSource(
				sourceDir.resolve( "test.module.annotations/test/module/annotations/ModuleMarker.java" ),
				"""
						package test.module.annotations;

						import java.lang.annotation.ElementType;
						import java.lang.annotation.Retention;
						import java.lang.annotation.RetentionPolicy;
						import java.lang.annotation.Target;

						@Target(ElementType.MODULE)
						@Retention(RetentionPolicy.RUNTIME)
						public @interface ModuleMarker {
							String value();
						}
						"""
		);
		writeSource(
				sourceDir.resolve( "test.module.subject/module-info.java" ),
				"""
						@test.module.annotations.ModuleMarker("module-level")
						module test.module.subject {
							requires test.module.annotations;
						}
						"""
		);

		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertThat( compiler ).isNotNull();

		try (Stream<Path> sourceFiles = Files.walk( sourceDir )) {
			final List<String> args = Stream.concat(
					Stream.of(
							"--module-source-path",
							sourceDir.toString(),
							"-d",
							classesDir.toString()
					),
					sourceFiles
							.filter( (path) -> path.toString().endsWith( ".java" ) )
							.map( Path::toString )
			).toList();

			assertThat( compiler.run( null, null, null, args.toArray( String[]::new ) ) ).isEqualTo( 0 );
		}

		final ModuleFinder moduleFinder = ModuleFinder.of(
				classesDir.resolve( "test.module.annotations" ),
				classesDir.resolve( "test.module.subject" )
		);
		final Configuration configuration = ModuleLayer.boot()
				.configuration()
				.resolve(
						moduleFinder,
						ModuleFinder.of(),
						Set.of( "test.module.annotations", "test.module.subject" )
				);
		return ModuleLayer.boot()
				.defineModulesWithOneLoader( configuration, ClassLoader.getSystemClassLoader() );
	}

	private static void writeSource(Path path, String source) throws IOException {
		Files.createDirectories( path.getParent() );
		Files.writeString( path, source, StandardCharsets.UTF_8 );
	}

	private static class ModuleLayerClassLoading implements ClassLoading {
		private final ModuleLayer layer;

		private ModuleLayerClassLoading(ModuleLayer layer) {
			this.layer = layer;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> Class<T> classForName(String name) {
			final Class<T> found = findClassForName( name );
			if ( found == null ) {
				throw new UnknownClassException( "Unable to locate class - " + name );
			}
			return found;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> Class<T> findClassForName(String name) {
			for ( Module module : layer.modules() ) {
				try {
					return (Class<T>) layer.findLoader( module.getName() ).loadClass( name );
				}
				catch (ClassNotFoundException ignore) {
				}
			}
			return null;
		}

		@Override
		public URL locateResource(String resourceName) {
			return null;
		}

		@Override
		public <S> Collection<S> loadJavaServices(Class<S> serviceType) {
			return List.of();
		}
	}
}
