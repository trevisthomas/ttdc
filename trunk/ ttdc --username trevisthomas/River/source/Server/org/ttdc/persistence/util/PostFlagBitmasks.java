package org.ttdc.persistence.util;

public interface PostFlagBitmasks {
	long BITMASK_DELETED = 1L;
	long BITMASK_INF = 2L;
	long BITMASK_LEGACY = 4L;
	long BITMASK_LINK = 8L;
	long BITMASK_MOVIE = 16L;
	long BITMASK_NWS = 32L;
	long BITMASK_PRIVATE = 64L;
	long BITMASK_RATABLE = 128L;
	long BITMASK_REVIEW = 256L;
	long BITMASK_LOCKED = 512L; // There are none of these in the v7 at
										// the moment this has been added.
}
