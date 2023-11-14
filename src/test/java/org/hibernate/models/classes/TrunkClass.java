package org.hibernate.models.classes;

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@ClassMarker
public class TrunkClass extends RootClass {
	@MemberMarker
	private Integer value3;
	@Transient
	private Integer value4;
}
