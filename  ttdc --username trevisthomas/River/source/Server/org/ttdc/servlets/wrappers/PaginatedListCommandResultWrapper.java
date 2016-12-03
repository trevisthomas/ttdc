package org.ttdc.servlets.wrappers;

import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;

public class PaginatedListCommandResultWrapper<T> {
	private final PaginatedListCommandResult<T> delegate;

	public PaginatedListCommandResultWrapper(PaginatedListCommandResult<T> delegate) {
		this.delegate = delegate;
	}

	public PaginatedListWrapper<T> getResults() {
		return new PaginatedListWrapper<T>(delegate.getResults());
	}
}
