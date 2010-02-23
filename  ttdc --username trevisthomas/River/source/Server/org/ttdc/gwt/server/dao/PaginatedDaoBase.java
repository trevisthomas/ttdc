package org.ttdc.gwt.server.dao;

abstract class PaginatedDaoBase {
	private final static int DEFAULT_PAGE_SIZE = 20;
	private int pageSize = DEFAULT_PAGE_SIZE;
	private int currentPage = 1;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	protected int calculatePageStartIndex() {
		return (getCurrentPage() - 1)*getPageSize();
	}
}
