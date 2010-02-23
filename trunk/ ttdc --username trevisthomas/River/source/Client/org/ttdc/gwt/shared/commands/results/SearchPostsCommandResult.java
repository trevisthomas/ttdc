package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;

public class SearchPostsCommandResult implements CommandResult{
	private PaginatedList<GPost> results;
	private PaginatedList<GPost> secondaryResults;
	
	
	public SearchPostsCommandResult() {	}
	
	public SearchPostsCommandResult(PaginatedList<GPost> results) {
		this.results = results;
	}
	
	public SearchPostsCommandResult(PaginatedList<GPost> results, PaginatedList<GPost> secondaryResults) {
		this.results = results;
		this.secondaryResults = secondaryResults;
	}

	public PaginatedList<GPost> getResults() {
		return results;
	}

	public void setResults(PaginatedList<GPost> results) {
		this.results = results;
	}

	public PaginatedList<GPost> getSecondaryResults() {
		return secondaryResults;
	}

	public void setSecondaryResults(PaginatedList<GPost> secondaryResults) {
		this.secondaryResults = secondaryResults;
	}
}
