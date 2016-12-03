package org.ttdc.servlets.wrappers;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.commands.results.TopicCommandResult;

public class TopicCommandResultWrapper {
	private TopicCommandResult delegate;

	public TopicCommandResultWrapper(TopicCommandResult delegate) {
		this.delegate = delegate;
	}
	// private TopicCommandType type;

	public PaginatedListWrapper<GPost> getResults() {
		return new PaginatedListWrapper<GPost>(delegate.getResults());
	}
}
