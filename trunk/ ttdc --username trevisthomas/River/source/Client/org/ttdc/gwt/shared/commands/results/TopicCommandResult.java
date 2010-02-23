package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.shared.commands.TopicCommandType;
import org.ttdc.gwt.shared.util.PaginatedList;

public class TopicCommandResult implements CommandResult{
	private PaginatedList<GPost> results;
	private TopicCommandType type;
	
	public TopicCommandResult() {	}
	
	public TopicCommandResult(PaginatedList<GPost> results) {
		this.results = results;
	}
	
	public PaginatedList<GPost> getResults() {
		return results;
	}

	public void setResults(PaginatedList<GPost> results) {
		this.results = results;
	}

	public TopicCommandType getType() {
		return type;
	}

	public void setType(TopicCommandType type) {
		this.type = type;
	}
}
