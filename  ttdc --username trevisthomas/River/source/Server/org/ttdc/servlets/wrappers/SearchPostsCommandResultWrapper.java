package org.ttdc.servlets.wrappers;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.commands.results.SearchPostsCommandResult;

public class SearchPostsCommandResultWrapper {
	private SearchPostsCommandResult delegate;

	public SearchPostsCommandResultWrapper(SearchPostsCommandResult delegate) {
		this.delegate = delegate;
	}

	public PaginatedListWrapper<GPost> getResults() {
		return new PaginatedListWrapper<GPost>(delegate.getResults());
	}

	public PaginatedListWrapper<GPost> getSecondaryResults() {
		if (delegate.getSecondaryResults() != null) {
			return new PaginatedListWrapper<GPost>(delegate.getSecondaryResults());
		} else {
			return null;
		}

	}
}
