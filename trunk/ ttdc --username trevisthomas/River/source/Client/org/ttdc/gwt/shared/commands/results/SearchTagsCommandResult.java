package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.shared.util.PaginatedList;

/**
 * Trevis, you made a seperate command result for this just incase tags
 * needed different behavior from posts.
 * 
 * @author Trevis
 *
 */
public class SearchTagsCommandResult implements CommandResult{
	private PaginatedList<GTag> results;
	
	public SearchTagsCommandResult() {	}
	
	public SearchTagsCommandResult(PaginatedList<GTag> results) {
		this.results = results;
	}

	public PaginatedList<GTag> getResults() {
		return results;
	}

	public void setResults(PaginatedList<GTag> results) {
		this.results = results;
	}

}
