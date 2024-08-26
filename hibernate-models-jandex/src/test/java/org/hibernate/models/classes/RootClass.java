package org.hibernate.models.classes;

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@ClassMarker
@SubclassableMarker
public class RootClass {
	@MemberMarker
	private Integer value1;
	@Transient
	private Integer value2;
}
