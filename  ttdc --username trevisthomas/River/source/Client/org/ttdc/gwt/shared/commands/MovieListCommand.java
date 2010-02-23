package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortBy;

public class MovieListCommand extends Command<SearchPostsCommandResult>{
	private SortBy sortBy;
	private SortDirection sortDirection;
	private String personId;
	private int pageNumber = 1;
	
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public SortBy setSortBy() {
		return sortBy;
	}
	public void setSortBy(SortBy sortBy) {
		this.sortBy = sortBy;
	}
	public SortDirection getSortDirection() {
		return sortDirection;
	}
	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}

		
}
