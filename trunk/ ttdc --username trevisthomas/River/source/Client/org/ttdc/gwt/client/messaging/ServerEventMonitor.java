package org.ttdc.gwt.client.messaging;

import java.util.List;

import org.ttdc.gwt.client.constants.AppConstants;
import org.ttdc.gwt.client.messaging.person.PersonEvent;
import org.ttdc.gwt.client.messaging.person.PersonEventListener;
import org.ttdc.gwt.client.messaging.person.PersonEventType;
import org.ttdc.gwt.client.services.RpcService;
import org.ttdc.gwt.client.services.RpcServiceAsync;
import org.ttdc.gwt.shared.commands.CommandResultCallback;
import org.ttdc.gwt.shared.commands.ServerEventListCommand;
import org.ttdc.gwt.shared.commands.ServerEventOpenConnectionCommand;
import org.ttdc.gwt.shared.commands.results.ServerEventCommandResult;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * This class does the magic.  This is the code that polls the server for new server events.
* @author Trevis
 *
 */
public class ServerEventMonitor extends Timer implements PersonEventListener{
	private final RpcServiceAsync service = GWT.create(RpcService.class);
	//public static String connectionId;
	
	public ServerEventMonitor(){
		reinitialize();
		EventBus.getInstance().addListener(this);
	}
	
	public void reinitialize(){
		cancel();
		service.execute(new ServerEventOpenConnectionCommand(), new CommandResultCallback<ServerEventCommandResult>(){
			public void onSuccess(ServerEventCommandResult result) {
				setConnectionId(result.getConnectionId());
			}
		});
	}
	
	@Override
	public void run() {
		service.execute(new ServerEventListCommand(ConnectionId.getInstance().getConnectionId()), new CommandResultCallback<ServerEventCommandResult>(){
			@Override
			public void onFailure(Throwable caught) {
//				Window.alert("ServerEventMonitor caught an exception when receiving new server events: "+caught + " Server may be down.  Refresh your browser manually to attempt to reconnect.");
//				cancel();
				//Just ignore this and try again.
			}
			public void onSuccess(ServerEventCommandResult result) {
				fireEvents(result.getEvents());
				
				//Check to see if the the server session user id changed. if it did, refresh this browser!
				if(result.getPerson() != null && ConnectionId.getInstance().getCurrentUser() != null){
					if(!ConnectionId.getInstance().getCurrentUser().getPersonId().equals(result.getPerson().getPersonId())){
						Window.Location.reload();
					}
				}
			}
		});
	}
	public void fireEvents(List<Event<?,?>> events) {
		for(Object obj : events){
			processRawServerEvent(obj);
		}
	}
	private void processRawServerEvent(Object obj) {
		EventBus.fireEvent((Event<?,?>) obj);
	}
	
	public void setConnectionId(String connectionId) {
		ConnectionId.getInstance().setConnectionId(connectionId);
		scheduleRepeating(AppConstants.SERVER_MONITOR_POLL_RATE_IN_MS);
	}

	//TODO: trevis, this may not be necessary.  Now that the monitor is per client session
	//the session id doesnt need to change when the user does. 
	public void onPersonEvent(PersonEvent event) {
		if(event.getType().isUserChanged()){
			ConnectionId.getInstance().setCurrentUser(event.getSource());
			reinitialize();
		}
		else if(event.is(PersonEventType.USER_EARMKARK_COUNT_CHANGED) 
				&& event.getSource().getPersonId()
				.equals(ConnectionId.getInstance().getCurrentUser().getPersonId())){
			ConnectionId.getInstance().setCurrentUser(event.getSource());			
		}
		
	}
	
}
