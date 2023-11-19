/*
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.models.UnknownClassException;
import org.hibernate.models.internal.jandex.JandexBuilders;
import org.hibernate.models.internal.jdk.JdkBuilders;
import org.hibernate.models.internal.jdk.VoidClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassDetailsRegistry;
import org.hibernate.models.spi.SourceModelBuildingContext;

import org.jboss.jandex.IndexView;

import static org.hibernate.models.internal.ModelsClassLogging.MODELS_CLASS_LOGGER;

/**
 * Standard ClassDetailsRegistry implementation.
 *
 * @author Steve Ebersole
 */
public class ClassDetailsRegistryStandard extends AbstractClassDetailsRegistry {
	private final StandardClassDetailsBuilder standardClassDetailsBuilder;
	private final SourceModelBuildingContext context;

	public ClassDetailsRegistryStandard(SourceModelBuildingContext context) {
		this.context = context;
		this.standardClassDetailsBuilder = new StandardClassDetailsBuilder( JdkBuilders.DEFAULT_BUILDER, context.getJandexIndex() );

		classDetailsMap.put( VoidClassDetails.VOID_CLASS_DETAILS.getClassName(), VoidClassDetails.VOID_CLASS_DETAILS );
		classDetailsMap.put( VoidClassDetails.VOID_OBJECT_CLASS_DETAILS.getClassName(), VoidClassDetails.VOID_OBJECT_CLASS_DETAILS );
	}

	@Override
	public void addClassDetails(ClassDetails classDetails) {
		addClassDetails( classDetails.getClassName(), classDetails );
	}

	@Override
	public void addClassDetails(String name, ClassDetails classDetails) {
		classDetailsMap.put( name, classDetails );

		if ( classDetails.getSuperType() != null ) {
			List<ClassDetails> subTypes = subTypeClassDetailsMap.get( classDetails.getSuperType().getName() );
			//noinspection Java8MapApi
			if ( subTypes == null ) {
				subTypes = new ArrayList<>();
				subTypeClassDetailsMap.put( classDetails.getSuperType().getName(), subTypes );
			}
			subTypes.add( classDetails );
		}
	}

	@Override
	public ClassDetails resolveClassDetails(String name) {
		return resolveClassDetails( name, standardClassDetailsBuilder );
	}

	@Override
	protected ClassDetails createClassDetails(String name, ClassDetailsBuilder creator) {
		try {
			final ClassDetails created = creator.buildClassDetails( name, context );
			addClassDetails( name, created );
			return created;
		}
		catch (UnknownClassException e) {
			// see if it might be a package name...
			try {
				return creator.buildClassDetails( name + ".package-info", context );
			}
			catch (UnknownClassException noPackage) {
				throw e;
			}
		}
	}

	@Override
	protected ClassDetails createClassDetails(String name, ClassDetailsCreator creator) {
		try {
			final ClassDetails created = creator.createClassDetails( name );
			addClassDetails( name, created );
			return created;
		}
		catch (UnknownClassException e) {
			// see if it might be a package name...
			try {
				return creator.createClassDetails( name + ".package-info" );
			}
			catch (UnknownClassException noPackage) {
				throw e;
			}
		}
	}

	private static class StandardClassDetailsBuilder implements ClassDetailsBuilder {
		private final boolean tryJandex;
		private final ClassDetailsBuilder fallbackClassDetailsBuilder;

		public StandardClassDetailsBuilder(ClassDetailsBuilder fallbackClassDetailsBuilder, IndexView jandexIndex) {
			this.fallbackClassDetailsBuilder = fallbackClassDetailsBuilder;
			this.tryJandex = jandexIndex != null;

			if ( tryJandex ) {
				MODELS_CLASS_LOGGER.debug( "Starting StandardClassDetailsBuilder with Jandex support" );
			}
			else {
				MODELS_CLASS_LOGGER.debug( "Starting StandardClassDetailsBuilder without Jandex support" );
			}
		}

		@Override
		public ClassDetails buildClassDetails(String name, SourceModelBuildingContext buildingContext) {
			MODELS_CLASS_LOGGER.tracef( "Building ClassDetails - %s", name );
			if ( tryJandex ) {
				try {
					return JandexBuilders.buildClassDetailsStatic( name, buildingContext );
				}
				catch (UnknownClassException e) {
					// generally means the class is not in the Jandex index - try the fallback
				}
			}

			return fallbackClassDetailsBuilder.buildClassDetails( name, buildingContext );
		}
	}

	@Override
	public ClassDetailsRegistry makeImmutableCopy() {
		return new ClassDetailsRegistryImmutable( classDetailsMap, subTypeClassDetailsMap );
	}
}
