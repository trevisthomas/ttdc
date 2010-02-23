package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.services.CommandResult;

/**
 * Trevis you made this but never used it.
 * @author Trevis
 *
 */
public class MessageCommandResult implements CommandResult{
	private String message;

	public MessageCommandResult() {}
	
	public MessageCommandResult(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}
