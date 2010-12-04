package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.ForumActionType;

public class ForumTopicListCommand extends Command<PaginatedListCommandResult<GPost>>{
	private String forumId;
	private ForumActionType action = ForumActionType.LOAD_TOPIC_PAGE;
	private int currentPage = 1;
	private int pageSize = -1;
	
	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public ForumActionType getAction() {
		return action;
	}

	public void setAction(ForumActionType action) {
		this.action = action;
	}

	public String getForumId() {
		return forumId;
	}

	public void setForumId(String forumId) {
		this.forumId = forumId;
	}
	
	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
}
