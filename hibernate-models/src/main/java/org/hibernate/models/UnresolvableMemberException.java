/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models;

import org.hibernate.models.spi.MemberDetails;

/**
 * Indicates a failed attempt to discover a corresponding reader/writer from a current member details
 * representing a writer/reader.
 *
 * @see MemberDetails#resolveValueWriter()
 * @see MemberDetails#resolveValueReader()
 */
public class UnresolvableMemberException extends ModelsException {
	public UnresolvableMemberException(String message) {
		super(message);
	}

	public UnresolvableMemberException(String message, Throwable cause) {
		super(message, cause);
	}
}
