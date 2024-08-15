package org.hibernate.models.classes;

import jakarta.persistence.Transient;

/**
 * @author Steve Ebersole
 */
@ClassMarker
public class BranchClass extends TrunkClass implements Intf {
	@MemberMarker
	private Integer value5;
	@Transient
	private Integer value6;
}
