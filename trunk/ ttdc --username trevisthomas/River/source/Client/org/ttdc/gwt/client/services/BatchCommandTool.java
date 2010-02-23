package org.ttdc.gwt.client.services;

import java.util.ArrayList;

import org.ttdc.gwt.client.messaging.EventBus;

import com.google.gwt.user.client.rpc.AsyncCallback;

 public class BatchCommandTool implements AsyncCallback<ArrayList<CommandResult>>{
	private final ArrayList<Command<CommandResult>> actionList = new ArrayList<Command<CommandResult>>();
	private final ArrayList<AsyncCallback<CommandResult>> responseList = new ArrayList<AsyncCallback<CommandResult>>();
	
	public void add(Command<? extends CommandResult> action, AsyncCallback<? extends CommandResult> callback){
		actionList.add((Command)action);
		responseList.add((AsyncCallback)callback);
	}
	public void onFailure(Throwable caught) {
		EventBus.getInstance().fireEvent(caught);
	}
	public void onSuccess(ArrayList<CommandResult> result) {
		int index = 0;
		for(CommandResult response : result){
			responseList.get(index++).onSuccess(response);
		}
		
	}
	public ArrayList<Command<CommandResult>> getActionList() {
		return actionList;
	}
}
