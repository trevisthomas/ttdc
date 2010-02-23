package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PersonListCommandResult;
import org.ttdc.gwt.shared.commands.types.PersonListType;
import org.ttdc.gwt.shared.commands.types.SortDirection;
import org.ttdc.gwt.shared.commands.types.SortBy;

public class PersonListCommand extends Command<PersonListCommandResult>{
	private PersonListType type;
	private int currentPage = 1;
	private SortBy sortBy = null;
	private SortDirection sortDirection = null;
	private boolean loadFullDetails;
	private int pageSize;

	public PersonListCommand() {}
	public PersonListCommand(PersonListType type) {
		this.type = type;
	}
	
	public boolean isMovieReviewerRequest(){
		return PersonListType.MOVIE_REVIEWERS.equals(type);
	}
	
	public PersonListType getType() {
		return type;
	}
	
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}
	public SortBy getSortOrder() {
		return sortBy;
	}
	public void setSortOrder(SortBy sortBy) {
		this.sortBy = sortBy;
	}
	public SortDirection getSortDirection() {
		return sortDirection;
	}
	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}
	public boolean isLoadFullDetails() {
		return loadFullDetails;
	}
	public void setLoadFullDetails(boolean loadFullDetails) {
		this.loadFullDetails = loadFullDetails;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
	
	
}
