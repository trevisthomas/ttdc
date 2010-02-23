package org.ttdc.gwt.client.services;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("rpc")
public interface RpcService extends RemoteService{
	<T extends CommandResult> T execute(Command<T> action) throws RemoteServiceException;
	<T extends CommandResult> ArrayList<T> execute(ArrayList<Command<T>> actionList) throws RemoteServiceException;
	<T extends CommandResult> T authenticate(String login, String password) throws RemoteServiceException;
	<T extends CommandResult> T logout() throws RemoteServiceException;
	<T extends CommandResult> T login(String personId, String encryptedPassword) throws RemoteServiceException;
	<T extends CommandResult> T identity(String personId) throws RemoteServiceException;
}
