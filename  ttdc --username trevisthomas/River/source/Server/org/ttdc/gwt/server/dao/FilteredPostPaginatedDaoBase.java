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
		//Having a list with "" breaks SQLServer2008! So i return this fake guid all the time. 
		//Research this, mabye i should just reformat the query to leave the filter value out when i dont have a real one?
		//filterThreadIds.add("");
		filterThreadIds.add("97605236-9CA3-4802-8C1E-C7435BC1BE27");
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
