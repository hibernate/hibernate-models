/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright: Red Hat Inc. and Hibernate Authors
 */
package org.hibernate.models.spi;

/**
 * Contract for providing custom {@link ClassDetailsBuilder} handling into the processing of
 * {@link ClassDetailsRegistry#resolveClassDetails} for a Java type we have not yet seen.
 *
 * @apiNote {@linkplain java.util.ServiceLoader Loadable service} for extensibility.
 *
 * @author Steve Ebersole
 */
public interface ClassDetailsBuilderProvider {
	/**
	 * Provide a specialized {@linkplain ClassDetailsBuilder} for this provider, or {@code null}
	 *
	 * @eturn
	 * if none (e.g., the Jandex provider might return null here to indicate that there is no Jandex index available).
	 */
	ClassDetailsBuilder provideBuilder(SourceModelBuildingContext buildingContext);
}
