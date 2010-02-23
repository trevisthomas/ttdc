package org.ttdc.gwt.shared.commands.results;

import java.util.List;

import org.ttdc.gwt.client.beans.GBase;
import org.ttdc.gwt.client.services.CommandResult;

public class GenericListCommandResult <T extends GBase> implements CommandResult {
	private List<T> list;
	private String message;
	
	public GenericListCommandResult(List<T> list, String message) {
		this.list = list;
		this.message = message;
	}
	
	public GenericListCommandResult(List<T> list) {
		this.list = list;
		this.message = "Success.";
	}
	
	public GenericListCommandResult() {}
	
	public List<T> getList() {
		return list;
	}
	
	public String getMessage() {
		return message;
	}
}
