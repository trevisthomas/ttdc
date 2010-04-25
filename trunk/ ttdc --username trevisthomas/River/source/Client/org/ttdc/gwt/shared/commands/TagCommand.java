package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.beans.GTag;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.GenericListCommandResult;
import org.ttdc.gwt.shared.commands.types.TagActionType;

public class TagCommand extends Command<GenericListCommandResult<GTag>>{
	private TagActionType action;

	public TagCommand() {
		// TODO Auto-generated constructor stub
	}
	
	public TagCommand(TagActionType action) {
		setAction(action);
	}
	
	public TagActionType getAction() {
		return action;
	}

	public void setAction(TagActionType action) {
		this.action = action;
	}
	
}
