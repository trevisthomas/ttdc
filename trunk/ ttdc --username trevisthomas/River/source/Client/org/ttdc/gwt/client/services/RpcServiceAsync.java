package org.ttdc.gwt.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface RpcServiceAsync {
	<T extends CommandResult> void execute(Command<T> action, AsyncCallback<T> callback);
	<T extends CommandResult> void execute(ArrayList<Command<T>> actionList, 
											 AsyncCallback<ArrayList<T>> callback);
	
	<T extends CommandResult> void authenticate(String login, String password, AsyncCallback<T> callback);
	<T extends CommandResult> void login(String personId, String encryptedPassword, AsyncCallback<T> callback);
	<T extends CommandResult> void logout(AsyncCallback<T> callback);
	<T extends CommandResult> void identity(String personId, AsyncCallback<T> callback);
	
}
