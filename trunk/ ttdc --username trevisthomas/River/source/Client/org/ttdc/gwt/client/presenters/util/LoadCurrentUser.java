package org.ttdc.gwt.client.presenters.util;

import org.ttdc.gwt.client.beans.GPerson;
import org.ttdc.gwt.client.messaging.ConnectionId;
import org.ttdc.gwt.client.messaging.EventBus;
import org.ttdc.gwt.client.services.RpcService;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.results.PersonCommandResult;

import com.google.gwt.core.client.GWT;

/**
 * This crazy class will grab the current user from {@link ConnectionId} and if it is not 
 * there it will make an async call to the server to grab either the user from session
 * or the user that matches the cookie credentials.
 *
 */
public class LoadCurrentUser {
	
	public interface CurrentUserResponse{
		void setCurrentUser(GPerson person);
	}
	
	public static void load(final CurrentUserResponse requester){
		GPerson person = ConnectionId.getInstance().getCurrentUser();
		if(person != null){
			requester.setCurrentUser(person);
		}
		else{
			RpcServiceAsync service = GWT.create(RpcService.class);
			CommandResultCallback<PersonCommandResult> callback = new CommandResultCallback<PersonCommandResult>(){
				public void onSuccess(PersonCommandResult result) {
					ConnectionId.getInstance().setCurrentUser(result.getPerson());
					requester.setCurrentUser(result.getPerson());
				}
				@Override
				public void onFailure(Throwable caught) {
					CookieTool.clear();
				}
			};
			service.login(CookieTool.readGuid(),CookieTool.readPwd(), callback);
		}
	}
}
