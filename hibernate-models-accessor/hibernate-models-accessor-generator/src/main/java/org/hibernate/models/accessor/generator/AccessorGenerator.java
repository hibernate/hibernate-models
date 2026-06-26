/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hibernate.models.accessor.generator.AccessorClassMetadata.FieldMetadata;
import org.hibernate.models.accessor.generator.impl.BridgeGenerator;
import org.hibernate.models.accessor.generator.impl.FactoryGenerator;
import org.hibernate.models.accessor.generator.impl.GeneratorConstants;
import org.hibernate.models.accessor.generator.impl.HostClassTransformer;
import org.hibernate.models.accessor.generator.impl.ProcessedHostData;
import org.hibernate.models.accessor.generator.impl.SingleImplGenerator;

public final class AccessorGenerator {

	public record GenerationInput(AccessorClassMetadata metadata, byte[] originalBytecode) {
	}

	public record GenerationResult(
			List<GeneratedClassResult> generatedClasses,
			List<GeneratedClassResult> transformedClasses) {
	}

	public static GenerationResult generate(List<GenerationInput> inputs) {
		List<GenerationInput> sorted = new ArrayList<>( inputs );
		sorted.sort( (a, b) -> a.metadata().compareTo( b.metadata() ) );

		List<ProcessedHostData> hosts = new ArrayList<>();
		ProcessedHostData currentType = null;

		for ( GenerationInput input : sorted ) {
			AccessorClassMetadata metadata = input.metadata();

			if ( currentType == null || !metadata.getType().name().equals( currentType.type().name() ) ) {
				currentType = new ProcessedHostData( metadata.getType() );
				hosts.add( currentType );
			}

			for ( FieldMetadata field : metadata.getFields() ) {
				currentType.readers().add( field );
				if ( !field.readOnly() ) {
					currentType.writers().add( field );
				}
			}

			currentType.readers().addAll( metadata.getGetters() );
			currentType.writers().addAll( metadata.getSetters() );
			currentType.constructors().addAll( metadata.getConstructors() );
		}

		FactoryGenerator factoryGen = new FactoryGenerator();
		BridgeGenerator bridgeGen = new BridgeGenerator();

		List<GeneratedClassResult> generatedClasses = new ArrayList<>();
		List<GeneratedClassResult> transformedClasses = new ArrayList<>();

		int currentClassIndex = 0;
		for ( int h = 0; h < hosts.size(); h++ ) {
			ProcessedHostData data = hosts.get( h );
			boolean needsBridge = !data.type().isPublic() && !data.type().isInterface();
			String dispatchTarget = needsBridge
					? BridgeGenerator.bridgeFqcn( data.type().host() )
					: data.type().host();
			String dispatchTargetInternal = dispatchTarget.replace( '.', '/' );

			factoryGen.registerDispatchTarget( data.type().host(), dispatchTargetInternal, data.type().isInterface() );
			factoryGen.registerFieldReader( data.type().host() );
			factoryGen.registerMethodReader( data.type().host() );
			factoryGen.registerFieldWriter( data.type().host() );
			factoryGen.registerMethodWriter( data.type().host() );
			factoryGen.registerInstantiator( data.type().host() );

			if ( needsBridge ) {
				String bridgeFqcn = BridgeGenerator.bridgeFqcn( data.type().host() );
				generatedClasses.add( new GeneratedClassResult( bridgeFqcn, bridgeGen.generate( data.type().host() ) ) );
			}

			byte[] originalBytecode = findOriginalBytecode( sorted, data.type().host() );
			if ( originalBytecode != null ) {
				HostClassTransformer transformer = new HostClassTransformer(
						data.readers(), data.writers(), data.constructors(), currentClassIndex );
				byte[] transformed = transformer.transform( originalBytecode );
				transformedClasses.add( new GeneratedClassResult( data.type().host(), transformed ) );
			}

			currentClassIndex++;
		}

		SingleImplGenerator implGen = new SingleImplGenerator();

		generatedClasses.add( new GeneratedClassResult(
				GeneratorConstants.GENERATED_READER_IMPL,
				implGen.generateReaderImpl( hosts ) ) );
		generatedClasses.add( new GeneratedClassResult(
				GeneratorConstants.GENERATED_WRITER_IMPL,
				implGen.generateWriterImpl( hosts ) ) );
		generatedClasses.add( new GeneratedClassResult(
				GeneratorConstants.GENERATED_INSTANTIATOR_IMPL,
				implGen.generateInstantiatorImpl( hosts ) ) );

		generatedClasses.add( new GeneratedClassResult(
				GeneratorConstants.GENERATED_FACTORY_FQCN,
				factoryGen.generate() ) );

		return new GenerationResult(
				Collections.unmodifiableList( generatedClasses ),
				Collections.unmodifiableList( transformedClasses ) );
	}

	private static byte[] findOriginalBytecode(List<GenerationInput> inputs, String hostFqcn) {
		for ( GenerationInput input : inputs ) {
			if ( input.metadata().getType().host().equals( hostFqcn ) ) {
				return input.originalBytecode();
			}
		}
		return null;
	}

	private AccessorGenerator() {
	}
}
