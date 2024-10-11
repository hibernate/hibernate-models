/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serial;

import org.hibernate.models.spi.ClassDetailsBuilder;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.RegistryPrimer;

/**
 * Standard SourceModelBuildingContext implementation
 *
 * @author Steve Ebersole
 */
public class BasicModelBuildingContextImpl extends AbstractModelBuildingContext {
	private transient AnnotationDescriptorRegistryStandard descriptorRegistry;
	private transient ClassDetailsRegistryStandard classDetailsRegistry;

	public BasicModelBuildingContextImpl(ClassLoading classLoadingAccess) {
		this( classLoadingAccess, null );
	}

	public BasicModelBuildingContextImpl(ClassLoading classLoadingAccess, RegistryPrimer registryPrimer) {
		super( classLoadingAccess );

		this.descriptorRegistry = new AnnotationDescriptorRegistryStandard( this );
		this.classDetailsRegistry = new ClassDetailsRegistryStandard( this );

		primeRegistries( registryPrimer );
	}

	@Override
	public MutableAnnotationDescriptorRegistry getAnnotationDescriptorRegistry() {
		return descriptorRegistry;
	}

	@Override
	public MutableClassDetailsRegistry getClassDetailsRegistry() {
		return classDetailsRegistry;
	}

	@Serial
	private void writeObject(ObjectOutputStream outputStream) throws IOException {
		outputStream.writeObject( classDetailsRegistry.getClassDetailsBuilder() );

		descriptorRegistry.serialize( outputStream, this );
		classDetailsRegistry.serialize( outputStream, this );

		outputStream.flush();
	}


	@Serial
	private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException {
		descriptorRegistry = new AnnotationDescriptorRegistryStandard( this );
		classDetailsRegistry = new ClassDetailsRegistryStandard( (ClassDetailsBuilder) inputStream.readObject(), this );

		descriptorRegistry.deserialize( inputStream, this );
		classDetailsRegistry.deserialize( inputStream, this );
	}
}
