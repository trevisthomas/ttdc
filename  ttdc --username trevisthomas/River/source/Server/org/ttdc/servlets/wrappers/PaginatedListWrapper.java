package org.ttdc.servlets.wrappers;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.shared.util.PaginatedList;

public class PaginatedListWrapper<T> {
	private final PaginatedList<T> delegate;

	// private final int totalResults;
	// private final List<PostWrapper> list = new ArrayList<PostWrapper>();
	// private final int currentPage;
	// private final int pageSize;
	// private final String phrase;

	public PaginatedListWrapper(PaginatedList<T> delegate) {
		this.delegate = delegate;
	}

	public int getTotalResults() {
		return delegate.getTotalResults();
	}

	public List<PostWrapper> getList() {
		List<PostWrapper> list = new ArrayList<PostWrapper>(delegate.getList().size());

		for (T p : delegate.getList()) {
			// lol, todo, um, this is not going to work if you paginate anything else :]
			list.add(new PostWrapper((GPost) p));
		}
		return list;
	}

	public int getCurrentPage() {
		return delegate.getCurrentPage();
	}

	public int getPageSize() {
		return delegate.getPageSize();
	}
}
