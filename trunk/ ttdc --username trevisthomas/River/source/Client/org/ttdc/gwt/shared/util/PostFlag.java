/**
 * Flag values for post index.  Used to filter posts in search.
 */
package org.ttdc.gwt.shared.util;

public enum PostFlag{
	
	DELETED(PostFlagBitmasks.BITMASK_DELETED),
	INF(PostFlagBitmasks.BITMASK_INF),
	LEGACY(PostFlagBitmasks.BITMASK_LEGACY),
	LINK(PostFlagBitmasks.BITMASK_LINK),
	MOVIE(PostFlagBitmasks.BITMASK_MOVIE),
	NWS(PostFlagBitmasks.BITMASK_NWS),
	PRIVATE(PostFlagBitmasks.BITMASK_PRIVATE),
	RATABLE(PostFlagBitmasks.BITMASK_RATABLE),
	REVIEW(PostFlagBitmasks.BITMASK_REVIEW),
	LOCKED(PostFlagBitmasks.BITMASK_LOCKED),
	;
	
	private final long bitmask;
	
	PostFlag(long bitmask){
		this.bitmask = bitmask;
	}

	public long getBitmask() {
		return bitmask;
	}
	
	
}