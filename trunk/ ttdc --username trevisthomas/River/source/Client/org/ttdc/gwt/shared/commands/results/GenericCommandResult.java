package org.ttdc.gwt.shared.commands.results;

import org.ttdc.gwt.client.beans.GBase;
import org.ttdc.gwt.client.services.CommandResult;

public class GenericCommandResult<T extends GBase> implements CommandResult {
	private T object;
	private String message;
	
	public GenericCommandResult(T object, String message) {
		this.object = object;
		this.message = message;
	}
	
	public GenericCommandResult() {}
	
	public T getObject() {
		return object;
	}
	
	public String getMessage() {
		return message;
	}
	
}
