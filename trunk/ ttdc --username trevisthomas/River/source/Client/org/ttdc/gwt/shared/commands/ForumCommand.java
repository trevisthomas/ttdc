package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GForum;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.ForumActionType;

public class ForumCommand extends Command<GenericListCommandResult<GForum>>{
	private ForumActionType action = ForumActionType.LOAD_FORUMS;

	public ForumActionType getAction() {
		return action;
	}

	public void setAction(ForumActionType action) {
		this.action = action;
	}
}
