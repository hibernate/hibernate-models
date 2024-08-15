package org.hibernate.models.classes;

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@ClassMarker
public class LeafClass extends BranchClass {
	@MemberMarker
	private Integer value7;
	@Transient
	private Integer value8;
}
