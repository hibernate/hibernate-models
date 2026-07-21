/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import org.hibernate.models.Creator;
import org.jboss.logging.Logger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies the API available to a named consumer module.  The compiled source deliberately
 * imports only the root and SPI packages; implementation packages must not be required for
 * ordinary model construction or consumption.
 */
public class ModuleBoundaryTests {
	@Test
	void consumerCanUseTheIntendedApiBoundary(@TempDir Path tempDir) throws Exception {
		final Path sourceRoot = tempDir.resolve( "src" );
		final Path classesRoot = tempDir.resolve( "classes" );
		final Path moduleInfo = sourceRoot.resolve( "orm.consumer/module-info.java" );
		final Path consumer = sourceRoot.resolve( "orm.consumer/orm/consumer/ModelsConsumer.java" );

		writeSource(
				moduleInfo,
				"""
						module orm.consumer {
							requires org.hibernate.models;
						}
						"""
		);
		writeSource(
				consumer,
				"""
						package orm.consumer;

						import java.lang.annotation.ElementType;
						import java.lang.annotation.Retention;
						import java.lang.annotation.RetentionPolicy;
						import java.lang.annotation.Target;
						import java.util.EnumSet;

						import org.hibernate.models.Creator;
						import org.hibernate.models.serial.spi.ModelsArchive;
						import org.hibernate.models.serial.spi.ModelsArchiveWriter;
						import org.hibernate.models.serial.spi.ModelsArchives;
						import org.hibernate.models.spi.AnnotationDescriptor;
						import org.hibernate.models.spi.AnnotationTarget;
						import org.hibernate.models.spi.ModelsContext;
						import org.hibernate.models.spi.MutableClassDetails;
						import org.hibernate.models.spi.MutableMemberDetails;
						import org.hibernate.models.spi.TypeDetails;

						@ModelsConsumer.Marker("valid")
						public class ModelsConsumer {
							public static void consume(ModelsContext modelsContext) {
								final MutableClassDetails jdkClass =
										Creator.createJdkClassDetails(String.class, modelsContext);
								final MutableClassDetails dynamicClass =
										Creator.createDynamicClassDetails("DynamicEntity", modelsContext);
								final TypeDetails stringType = TypeDetails.classType(jdkClass);
								final MutableMemberDetails member = Creator.createDynamicMemberDetails(
										"name",
										stringType,
										dynamicClass,
										false,
										false,
										modelsContext
								);
								dynamicClass.addField(member.asFieldDetails());

								final AnnotationDescriptor<Marker> descriptor =
										Creator.createAnnotationDescriptor(Marker.class, modelsContext);
								Creator.createCompleteAnnotationDescriptor(
										Marker.class,
										MutableMarker.class,
										EnumSet.of(AnnotationTarget.Kind.CLASS),
										false
								);

								descriptor.getAnnotationType();
								descriptor.validateUsage( ModelsConsumer.class.getAnnotation( Marker.class ), modelsContext );
								dynamicClass.render(modelsContext);
								member.asFieldDetails().render(modelsContext);

								ModelsArchiveWriter archiveWriter = ModelsArchives.createWriter(false);
								archiveWriter.reference(jdkClass);
								ModelsArchive archive = archiveWriter.finish();
							}

							@Target(ElementType.TYPE)
							@Retention(RetentionPolicy.RUNTIME)
							public @interface Marker {
								String value() default "";
							}

							public abstract static class MutableMarker implements Marker {
								public abstract void value(String value);
							}
						}
						"""
		);

		compileConsumerModule( classesRoot, moduleInfo, consumer );
	}

	private static void compileConsumerModule(Path classesRoot, Path... sources)
			throws IOException, URISyntaxException {
		final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		assertThat( compiler ).isNotNull();
		Files.createDirectories( classesRoot );

		final DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
		try (StandardJavaFileManager fileManager = compiler.getStandardFileManager( diagnostics, null, StandardCharsets.UTF_8 )) {
			fileManager.setLocationFromPaths(
					StandardLocation.MODULE_PATH,
					List.of( codeSourceOf( Creator.class ), codeSourceOf( Logger.class ) )
			);
			fileManager.setLocationFromPaths( StandardLocation.CLASS_OUTPUT, List.of( classesRoot ) );

			final var compilationUnits = fileManager.getJavaFileObjects( sources );
			final boolean compiled = compiler.getTask(
					null,
					fileManager,
					diagnostics,
					List.of(),
					null,
					compilationUnits
			).call();

			assertThat( compiled )
					.withFailMessage( () -> "Consumer module compilation failed:%n%s".formatted(
							diagnostics.getDiagnostics().stream()
									.map( Object::toString )
									.collect( java.util.stream.Collectors.joining( System.lineSeparator() ) )
					) )
					.isTrue();
		}
	}

	private static Path codeSourceOf(Class<?> type) throws URISyntaxException {
		return Path.of( type.getProtectionDomain().getCodeSource().getLocation().toURI() );
	}

	private static void writeSource(Path path, String source) throws IOException {
		Files.createDirectories( path.getParent() );
		Files.writeString( path, source, StandardCharsets.UTF_8 );
	}
}
