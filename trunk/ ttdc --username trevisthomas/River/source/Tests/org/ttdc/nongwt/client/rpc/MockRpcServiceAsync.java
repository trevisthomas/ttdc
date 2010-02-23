package org.ttdc.nongwt.client.rpc;

import java.util.ArrayList;
import java.util.List;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.client.services.RpcServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class MockRpcServiceAsync implements RpcServiceAsync{
	private final CommandResult response;
	private final List<CommandResult> responseList;
	private boolean disable = false;
	private Command command;
	public MockRpcServiceAsync(CommandResult response) {
		this.response = response;
		responseList = new ArrayList<CommandResult>();
	}
	
	
	public <T extends CommandResult> void execute(Command<T> action, AsyncCallback<T> callback) {
		this.command = action;
		if(!isDisable())
			callback.onSuccess((T)response);
	}
	public <T extends CommandResult> void execute(ArrayList<Command<T>> actionList, AsyncCallback<ArrayList<T>> callback) {
		if(!isDisable()){
			for(Command<T> a : actionList){
				responseList.add(response); //Creating the apporpriate number of mocks. should probably refactor to make the test provide this too
			}
			callback.onSuccess((ArrayList<T>)responseList);
		}
	}


	public <T extends CommandResult> void authenticate(String login, String password, AsyncCallback<T> callback) {
		// TODO Auto-generated method stub
		
	}


	
	public <T extends CommandResult> void logout(AsyncCallback<T> callback) {
		// TODO Auto-generated method stub
		
	}


	public boolean isDisable() {
		return disable;
	}


	public void setDisable(boolean disable) {
		this.disable = disable;
	}


	public Command getCommand() {
		return command;
	}


	@Override
	public <T extends CommandResult> void login(String personId, String encryptedPassword, AsyncCallback<T> callback) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <T extends CommandResult> void identity(String personId, AsyncCallback<T> callback) {
		// TODO Auto-generated method stub
		
	}


	

	
}
