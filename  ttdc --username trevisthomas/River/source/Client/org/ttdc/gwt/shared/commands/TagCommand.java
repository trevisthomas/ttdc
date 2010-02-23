package org.ttdc.gwt.shared.commands;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.shared.commands.results.TagCommandResult;
import org.ttdc.gwt.shared.commands.types.TagActionType;

public class TagCommand extends Command<TagCommandResult>{
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
