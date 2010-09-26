package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GPost;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.PaginatedListCommandResult;
import org.ttdc.gwt.shared.commands.types.ForumActionType;

public class ForumTopicListCommand extends Command<PaginatedListCommandResult<GPost>>{
	private String forumId;
	private ForumActionType action = ForumActionType.LOAD_TOPIC_PAGE;
	
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
}
