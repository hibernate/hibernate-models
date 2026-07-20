/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.serial.internal;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import org.hibernate.models.serial.spi.ModelReference;
import org.hibernate.models.spi.TypeDetails;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModelsArchiveValidationTests {
	private static final int MAGIC = 0x484D4F44;
	private static final int FORMAT_VERSION = 1;

	@Test
	void readExternalRejectsUnexpectedMagic() throws Exception {
		final ModelsArchiveImpl archive = new ModelsArchiveImpl();

		assertThatThrownBy( () -> archive.readExternal( inputForHeader( 0, FORMAT_VERSION ) ) )
				.isInstanceOf( java.io.InvalidObjectException.class )
				.hasMessageContaining( "magic" );
	}

	@Test
	void readExternalRejectsUnsupportedVersion() throws Exception {
		final ModelsArchiveImpl archive = new ModelsArchiveImpl();

		assertThatThrownBy( () -> archive.readExternal( inputForHeader( MAGIC, FORMAT_VERSION + 1 ) ) )
				.isInstanceOf( java.io.InvalidObjectException.class )
				.hasMessageContaining( "version" );
	}

	@Test
	void readExternalRejectsNegativeTableSize() throws Exception {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try ( ObjectOutputStream output = new ObjectOutputStream( bytes ) ) {
			output.writeInt( MAGIC );
			output.writeInt( FORMAT_VERSION );
			output.writeBoolean( false );
			output.writeInt( -1 );
		}

		final ModelsArchiveImpl archive = new ModelsArchiveImpl();

		assertThatThrownBy( () -> archive.readExternal( new ObjectInputStream( new ByteArrayInputStream( bytes.toByteArray() ) ) ) )
				.isInstanceOf( java.io.InvalidObjectException.class )
				.hasMessageContaining( "class-table size" );
	}

	@Test
	void readExternalRejectsWrongTableEntryType() throws Exception {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try ( ObjectOutputStream output = new ObjectOutputStream( bytes ) ) {
			output.writeInt( MAGIC );
			output.writeInt( FORMAT_VERSION );
			output.writeBoolean( false );
			output.writeInt( 0 );
			output.writeInt( 1 );
			output.writeObject( "not a type reference" );
		}

		final ModelsArchiveImpl archive = new ModelsArchiveImpl();

		assertThatThrownBy( () -> archive.readExternal( new ObjectInputStream( new ByteArrayInputStream( bytes.toByteArray() ) ) ) )
				.isInstanceOf( java.io.InvalidObjectException.class )
				.hasMessageContaining( "Invalid type-table entry at index 0" );
	}

	@Test
	void constructorRejectsInvalidNestedReferences() {
		assertThatThrownBy( () -> new ModelsArchiveImpl(
				false,
				List.of(),
				List.of( new ModelsArchiveImpl.ClassTypeReference( TypeDetails.Kind.CLASS, -1 ) ),
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of()
		) )
				.isInstanceOf( IllegalArgumentException.class )
				.hasMessageContaining( "class type class id is negative" );
	}

	@Test
	void constructorRejectsInvalidAnnotationUsageTarget() {
		assertThatThrownBy( () -> new ModelsArchiveImpl(
				false,
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of(),
				List.of( new ModelsArchiveImpl.AnnotationUsageReference(
						new ModelReference( ModelReference.Kind.CLASS, 0 ),
						" ",
						null,
						Map.of()
				) )
		) )
				.isInstanceOf( IllegalArgumentException.class )
				.hasMessageContaining( "annotation type name" );
	}

	private static ObjectInputStream inputForHeader(int magic, int version) throws Exception {
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try ( ObjectOutputStream output = new ObjectOutputStream( bytes ) ) {
			output.writeInt( magic );
			output.writeInt( version );
		}
		return new ObjectInputStream( new ByteArrayInputStream( bytes.toByteArray() ) );
	}
}
