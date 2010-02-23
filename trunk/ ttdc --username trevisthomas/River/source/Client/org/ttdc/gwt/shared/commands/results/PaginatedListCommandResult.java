package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;

public class PaginatedListCommandResult<T> implements CommandResult{
	private PaginatedList<T> results;
	
	public PaginatedListCommandResult(){}
	
	public PaginatedListCommandResult(PaginatedList<T> results){
		this.results = results;
	}
	
	public PaginatedList<T> getResults() {
		return results;
	}
}
