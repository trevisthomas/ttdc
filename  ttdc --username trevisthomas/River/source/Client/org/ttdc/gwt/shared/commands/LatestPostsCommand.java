package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.PostListType;

public class LatestPostsCommand extends Command<PaginatedListCommandResult<GPost>>{
	private PostListType action;
	private int pageNumber = 1;
	private int pageSize = -1;

	public void setAction(PostListType action) {
		this.action = action;
	}

	public LatestPostsCommand() {
	}
	
	public LatestPostsCommand(PostListType action) {
		this.action = action;
	}

	public PostListType getAction() {
		return action;
	}
	
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
