/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.accessor.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.models.accessor.generator.AccessorClassMetadata.FieldMetadata;
import org.hibernate.models.accessor.generator.AccessorClassMetadata.MemberMetadata;
import org.hibernate.models.accessor.generator.AccessorClassMetadata.MultiValueGroupMetadata;
import org.hibernate.models.accessor.generator.impl.BridgeGenerator;
import org.hibernate.models.accessor.generator.impl.FactoryGenerator;
import org.hibernate.models.accessor.generator.impl.GeneratorConstants;
import org.hibernate.models.accessor.generator.impl.HostClassTransformer;
import org.hibernate.models.accessor.generator.impl.HostClassTransformer.NameAndIndex;
import org.hibernate.models.accessor.generator.impl.MultiValueImplGenerator;
import org.hibernate.models.accessor.generator.impl.MultiValueImplGenerator.ProcessedMultiValueGroup;
import org.hibernate.models.accessor.generator.impl.MultiValueImplGenerator.ResolvedMemberAccess;
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
		return generate( inputs, List.of(), List.of() );
	}

	public static GenerationResult generate(List<GenerationInput> inputs,
			List<MultiValueGroupMetadata> readerGroups,
			List<MultiValueGroupMetadata> writerGroups) {
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

		// Build lookup maps for host data
		Map<String, ProcessedHostData> hostByName = new HashMap<>();
		Map<String, Integer> classIndexByName = new HashMap<>();
		Map<String, String> dispatchTargetByHost = new HashMap<>();
		Map<String, Boolean> isInterfaceByHost = new HashMap<>();

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

			hostByName.put( data.type().host(), data );
			classIndexByName.put( data.type().host(), currentClassIndex );
			dispatchTargetByHost.put( data.type().host(), dispatchTargetInternal );
			isInterfaceByHost.put( data.type().host(), data.type().isInterface() );

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

			currentClassIndex++;
		}

		// Process multi-value groups
		List<ProcessedMultiValueGroup> processedReaderGroups = processMultiValueGroups(
				readerGroups, hostByName, dispatchTargetByHost, isInterfaceByHost, true );
		List<ProcessedMultiValueGroup> processedWriterGroups = processMultiValueGroups(
				writerGroups, hostByName, dispatchTargetByHost, isInterfaceByHost, false );

		// Build per-host multi-value group maps for HostClassTransformer
		Map<String, List<NameAndIndex>> readerGroupsByHost = groupByTargetHost( readerGroups, processedReaderGroups );
		Map<String, List<NameAndIndex>> writerGroupsByHost = groupByTargetHost( writerGroups, processedWriterGroups );

		// Register multi-value target classes with factory
		for ( String targetClass : readerGroupsByHost.keySet() ) {
			factoryGen.registerMultiValueReader( targetClass );
		}
		for ( String targetClass : writerGroupsByHost.keySet() ) {
			factoryGen.registerMultiValueWriter( targetClass );
		}

		// Transform host classes
		for ( ProcessedHostData data : hosts ) {
			String hostFqcn = data.type().host();
			int hostClassIndex = classIndexByName.get( hostFqcn );

			List<NameAndIndex> hostReaderGroups = readerGroupsByHost.getOrDefault( hostFqcn, List.of() );
			List<NameAndIndex> hostWriterGroups = writerGroupsByHost.getOrDefault( hostFqcn, List.of() );

			byte[] originalBytecode = findOriginalBytecode( sorted, hostFqcn );
			if ( originalBytecode != null ) {
				HostClassTransformer transformer = new HostClassTransformer(
						data.readers(), data.writers(), data.constructors(), hostClassIndex,
						hostReaderGroups, hostWriterGroups );
				byte[] transformed = transformer.transform( originalBytecode );
				transformedClasses.add( new GeneratedClassResult( hostFqcn, transformed ) );
			}
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

		// Generate multi-value impl classes
		MultiValueImplGenerator multiValueGen = new MultiValueImplGenerator();

		if ( !processedReaderGroups.isEmpty() ) {
			generatedClasses.add( new GeneratedClassResult(
					GeneratorConstants.GENERATED_MULTI_VALUE_READER_IMPL,
					multiValueGen.generateMultiValueReaderImpl( processedReaderGroups ) ) );
		}
		if ( !processedWriterGroups.isEmpty() ) {
			generatedClasses.add( new GeneratedClassResult(
					GeneratorConstants.GENERATED_MULTI_VALUE_WRITER_IMPL,
					multiValueGen.generateMultiValueWriterImpl( processedWriterGroups ) ) );
		}

		generatedClasses.add( new GeneratedClassResult(
				GeneratorConstants.GENERATED_FACTORY_FQCN,
				factoryGen.generate() ) );

		return new GenerationResult(
				Collections.unmodifiableList( generatedClasses ),
				Collections.unmodifiableList( transformedClasses ) );
	}

	private static List<ProcessedMultiValueGroup> processMultiValueGroups(
			List<MultiValueGroupMetadata> groups,
			Map<String, ProcessedHostData> hostByName,
			Map<String, String> dispatchTargetByHost,
			Map<String, Boolean> isInterfaceByHost,
			boolean isReader) {
		List<ProcessedMultiValueGroup> processed = new ArrayList<>();
		for ( int i = 0; i < groups.size(); i++ ) {
			MultiValueGroupMetadata group = groups.get( i );
			List<ResolvedMemberAccess> resolvedMembers = new ArrayList<>();

			for ( MemberMetadata member : group.members() ) {
				String memberHost = member.declaringClass();
				ProcessedHostData hostData = hostByName.get( memberHost );
				if ( hostData == null ) {
					throw new IllegalArgumentException(
							"Multi-value group references member from unknown host class: " + memberHost
									+ " (member: " + member.name() + ")" );
				}

				Set<MemberMetadata> memberSet = isReader ? hostData.readers() : hostData.writers();
				int memberIndex = findMemberIndex( memberSet, member );

				resolvedMembers.add( new ResolvedMemberAccess(
						dispatchTargetByHost.get( memberHost ),
						isInterfaceByHost.get( memberHost ),
						memberIndex ) );
			}

			processed.add( new ProcessedMultiValueGroup( group.descriptor(), i, resolvedMembers ) );
		}
		return processed;
	}

	private static Map<String, List<NameAndIndex>> groupByTargetHost(
			List<MultiValueGroupMetadata> groups,
			List<ProcessedMultiValueGroup> processedGroups) {
		Map<String, List<NameAndIndex>> result = new HashMap<>();
		for ( int i = 0; i < groups.size(); i++ ) {
			String targetClass = groups.get( i ).targetDeclaringClass();
			result.computeIfAbsent( targetClass, k -> new ArrayList<>() )
					.add( new NameAndIndex( processedGroups.get( i ).descriptor(),
							processedGroups.get( i ).groupIndex() ) );
		}
		return result;
	}

	private static int findMemberIndex(Set<MemberMetadata> sortedSet, MemberMetadata target) {
		int index = 0;
		for ( MemberMetadata member : sortedSet ) {
			if ( member.compareTo( target ) == 0 ) {
				return index;
			}
			index++;
		}
		throw new IllegalArgumentException(
				"Member not found in host class: " + target.declaringClass() + "." + target.name() );
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
