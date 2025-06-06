/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.lang.annotation.Annotation;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hibernate.models.serial.spi.SerialAnnotationDescriptor;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.serial.spi.StorableContext;
import org.hibernate.models.spi.AnnotationDescriptor;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.ClassLoading;
import org.hibernate.models.spi.ModelsContext;

import static org.hibernate.models.internal.util.CollectionHelper.linkedMapOfSize;

/**
 * Standard implementation of {@linkplain StorableContext} representing a serializable {@linkplain ModelsContext}
 *
 * @author Steve Ebersole
 */
public class StorableContextImpl implements StorableContext {
	private final boolean trackImplementors;
	private final LinkedHashMap<String, SerialClassDetails> serialClassDetailsMap;
	private final LinkedHashMap<Class<? extends Annotation>, SerialAnnotationDescriptor<? extends Annotation>> serialAnnotationDescriptorMap;

	public StorableContextImpl(
			boolean trackImplementors,
			Map<String, ClassDetails> classDetailsMap,
			Map<Class<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> annotationDescriptorMap) {
		this.trackImplementors = trackImplementors;
		serialClassDetailsMap = linkedMapOfSize( classDetailsMap.size() );
		serialAnnotationDescriptorMap = linkedMapOfSize( annotationDescriptorMap.size() );

		for ( Map.Entry<String, ClassDetails> classDetailsEntry : classDetailsMap.entrySet() ) {
			serialClassDetailsMap.put( classDetailsEntry.getKey(), classDetailsEntry.getValue().toStorableForm() );
		}
		for ( Map.Entry<Class<? extends Annotation>, AnnotationDescriptor<? extends Annotation>> descriptorEntry : annotationDescriptorMap.entrySet() ) {
			serialAnnotationDescriptorMap.put( descriptorEntry.getKey(), descriptorEntry.getValue().toStorableForm() );
		}
	}

	@Override
	public ModelsContext fromStorableForm(ClassLoading classLoading) {
		return new RestoredModelContext( this, classLoading, trackImplementors );
	}

	public LinkedHashMap<String, SerialClassDetails> getSerialClassDetailsMap() {
		return serialClassDetailsMap;
	}

	public LinkedHashMap<Class<? extends Annotation>, SerialAnnotationDescriptor<? extends Annotation>> getSerialAnnotationDescriptorMap() {
		return serialAnnotationDescriptorMap;
	}
}
