/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.io.Serial;
import java.util.List;

import org.hibernate.models.internal.ClassTypeDetailsImpl;
import org.hibernate.models.internal.PrimitiveTypeDetailsImpl;
import org.hibernate.models.internal.VoidTypeDetailsImpl;
import org.hibernate.models.dynamic.DynamicClassDetails;
import org.hibernate.models.dynamic.DynamicFieldDetails;
import org.hibernate.models.serial.spi.SerialClassDetails;
import org.hibernate.models.spi.ClassDetails;
import org.hibernate.models.spi.FieldDetails;
import org.hibernate.models.spi.ModelsContext;
import org.hibernate.models.spi.TypeDetails;

/**
 * Serial representation of a dynamic class.
 *
 * @author Steve Ebersole
 */
public class SerialDynamicClassDetails implements SerialClassDetails {
	@Serial
	private static final long serialVersionUID = 1L;

	private final String name;
	private final String className;
	private final boolean isAbstract;
	private final String superClassName;
	private final List<SerialDynamicField> fields;

	public SerialDynamicClassDetails(DynamicClassDetails classDetails) {
		this.name = classDetails.getName();
		this.className = classDetails.getClassName();
		this.isAbstract = classDetails.isAbstract();
		this.superClassName = classDetails.getSuperClass() == null ? null : classDetails.getSuperClass().getName();
		this.fields = classDetails.getFields().stream().map( SerialDynamicField::new ).toList();
		if ( !classDetails.getMethods().isEmpty() ) {
			throw new UnsupportedOperationException( "Dynamic method archive entries are not implemented yet" );
		}
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getClassName() {
		return className;
	}

	@Override
	public ClassDetails toClassDetails(ModelsContext context) {
		final ClassDetails superClass = superClassName == null
				? null
				: context.getClassDetailsRegistry().resolveClassDetails( superClassName );
		final DynamicClassDetails classDetails = new DynamicClassDetails(
				name,
				className,
				isAbstract,
				superClass,
				null,
				context
		);
		for ( SerialDynamicField field : fields ) {
			classDetails.addField( field.toFieldDetails( classDetails, context ) );
		}
		return classDetails;
	}

	private record SerialDynamicField(
			String name,
			String typeName,
			TypeDetails.Kind typeKind,
			int modifierFlags,
			boolean isArray,
			boolean isPlural) implements java.io.Serializable {
		@Serial
		private static final long serialVersionUID = 1L;

		private SerialDynamicField(FieldDetails fieldDetails) {
			this(
					fieldDetails.getName(),
					fieldDetails.getType().determineRawClass().getName(),
					fieldDetails.getType().getTypeKind(),
					fieldDetails.getModifiers(),
					fieldDetails.isArray(),
					fieldDetails.isPlural()
			);
		}

		private FieldDetails toFieldDetails(DynamicClassDetails declaringType, ModelsContext context) {
			final ClassDetails rawClass = context.getClassDetailsRegistry().resolveClassDetails( typeName );
			final TypeDetails typeDetails = switch ( typeKind ) {
				case CLASS -> new ClassTypeDetailsImpl( rawClass, TypeDetails.Kind.CLASS );
				case PRIMITIVE -> new PrimitiveTypeDetailsImpl( rawClass );
				case VOID -> new VoidTypeDetailsImpl( rawClass );
				default -> throw new UnsupportedOperationException(
						"Unsupported dynamic field type kind in archive: " + typeKind
				);
			};
			return new DynamicFieldDetails(
					name,
					typeDetails,
					declaringType,
					modifierFlags,
					isArray,
					isPlural,
					context
			);
		}
	}
}
