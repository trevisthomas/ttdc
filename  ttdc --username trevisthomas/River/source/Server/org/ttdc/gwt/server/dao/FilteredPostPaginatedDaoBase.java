package org.ttdc.gwt.server.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ttdc.gwt.shared.util.PostFlag;

public class FilteredPostPaginatedDaoBase extends PaginatedDaoBase{
	private Set<PostFlag> filterFlags = new HashSet<PostFlag>();
	private List<String> filterThreadIds = new ArrayList<String>(); 
	
	public void addFlagFilter(PostFlag flag){
		filterFlags.add(flag);
	}
	public Set<PostFlag> getFilterFlags() {
		return filterFlags;
	}

	public void setFilterFlags(Set<PostFlag> filterFlags) {
		this.filterFlags = filterFlags;
	}
	
	long buildFilterMask(Set<PostFlag> filterFlags){
		long filterMask = 0;
		for(PostFlag flag : filterFlags){
			filterMask = filterMask | flag.getBitmask();
		}
		return filterMask;
	}
	
	public FilteredPostPaginatedDaoBase() {
		filterThreadIds.add("");
	}
	
	public List<String> getFilterThreadIds() {
		return filterThreadIds;
	}

	public void addFilterThreadIds(List<String> filterThreadIds){
		this.filterThreadIds.addAll(filterThreadIds);
	}
	
	public void addFilterThreadId(String threadId){
		this.filterThreadIds.add(threadId);
	}
}
