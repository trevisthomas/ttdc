package org.ttdc.gwt.server.dao;

import java.util.HashSet;
import java.util.Set;

import org.ttdc.gwt.shared.util.PostFlag;

public class FilteredPostPaginatedDaoBase extends PaginatedDaoBase{
	private Set<PostFlag> filterFlags = new HashSet<PostFlag>();
	
	public void addFlagFilter(PostFlag flag){
		filterFlags.add(flag);
	}
	public Set<PostFlag> getFilterFlags() {
		return filterFlags;
	}

	public void setFilterFlags(Set<PostFlag> filterFlags) {
		this.filterFlags = filterFlags;
	}
	
	protected long buildFilterMask(Set<PostFlag> filterFlags){
		long filterMask = 0;
		for(PostFlag flag : filterFlags){
			filterMask = filterMask | flag.getBitmask();
		}
		return filterMask;
	}
}
