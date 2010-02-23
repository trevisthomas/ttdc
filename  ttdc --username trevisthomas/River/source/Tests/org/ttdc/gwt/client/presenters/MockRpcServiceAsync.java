package org.ttdc.gwt.client.presenters;

import java.util.ArrayList;

import org.ttdc.gwt.client.services.Command;
import org.ttdc.gwt.client.services.CommandResult;
import org.ttdc.gwt.client.services.RpcServiceAsync;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class MockRpcServiceAsync implements RpcServiceAsync{
	public Command command;
	public AsyncCallback<?> callback;
	
	public <T extends CommandResult> void authenticate(String login, String password, AsyncCallback<T> callback) {
		// TODO Auto-generated method stub
		
	}

	public <T extends CommandResult> void execute(Command<T> action, AsyncCallback<T> callback) {
		command = action;
		this.callback = callback;
	}

	public <T extends CommandResult> void execute(ArrayList<Command<T>> actionList, AsyncCallback<ArrayList<T>> callback) {
		// TODO Auto-generated method stub
		
	}

	public <T extends CommandResult> void logout(AsyncCallback<T> callback) {
		// TODO Auto-generated method stub
		
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
