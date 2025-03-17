/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.testing.tests.annotations.target;

/**
 * @author Steve Ebersole
 */
public class MemberGeneratorEntity {
	@GeneratorAnnotation(GeneratorAnnotation.Source.MEMBER)
	private Integer id;
}
